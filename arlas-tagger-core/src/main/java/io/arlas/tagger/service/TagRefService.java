/*
 * Licensed to Gisaïa under one or more contributor
 * license agreements. See the NOTICE.txt file distributed with
 * this work for additional information regarding copyright
 * ownership. Gisaïa licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.arlas.tagger.service;

import io.arlas.commons.exceptions.ArlasException;
import io.arlas.commons.exceptions.InvalidParameterException;
import io.arlas.commons.exceptions.NotAllowedException;
import io.arlas.commons.exceptions.NotFoundException;
import io.arlas.server.core.model.CollectionReference;
import io.arlas.server.core.model.enumerations.AggregationTypeEnum;
import io.arlas.server.core.model.enumerations.OperatorEnum;
import io.arlas.server.core.model.request.*;
import io.arlas.server.core.model.response.AggregationResponse;
import io.arlas.server.core.utils.ParamsParser;
import io.arlas.tagger.app.ArlasTaggerConfiguration;
import io.arlas.tagger.kafka.TagKafkaProducer;
import io.arlas.tagger.model.TaggingStatus;
import io.arlas.tagger.model.request.TagRefRequest;
import io.arlas.tagger.model.response.UpdateResponse;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * /!\ With the current implementation, the topic read by TagRefService *MUST* have only 1 partition.
 */
public class TagRefService extends KafkaConsumerRunner {
    private final Logger LOGGER = LoggerFactory.getLogger(TagRefService.class);
    private final TagKafkaProducer tagKafkaProducer;
    private final UpdateServices updateServices;
    private final Long statusTimeout;

    public TagRefService(ArlasTaggerConfiguration configuration, String topic, String consumerGroupId,
                         TagKafkaProducer tagKafkaProducer, UpdateServices updateServices) {
        super(1, configuration, topic, consumerGroupId, configuration.kafkaConfiguration.batchSizeTagRef);
        this.tagKafkaProducer = tagKafkaProducer;
        this.updateServices = updateServices;
        this.statusTimeout = configuration.statusTimeout;
    }

    @Override
    public void setReplayFromOffset(long replayFromOffset) {
        this.replayFromOffset = replayFromOffset;
    }

    @Override
    public void processRecords(ConsumerRecords<String, String> records) {
        long start = System.currentTimeMillis();
        for (ConsumerRecord<String, String> record : records) {

            try {
                final TagRefRequest tagRequest = MAPPER.readValue(record.value(), TagRefRequest.class);
                if (tagRequest.propagation == null) {
                    LOGGER.debug("No propagation requested: {}", record.value());

                    tagRequest.propagated = 1; // only one request
                    tagKafkaProducer.sendToExecuteTags(null, tagRequest);
                    LOGGER.trace("Sent to Kafka with processtime={}ms and no key", (System.currentTimeMillis() - start));
                } else {
                    LOGGER.debug("Propagation requested: " + record.value());

                    long t0 = System.currentTimeMillis();
                    AggregationResponse aggregationResponse = getArlasAggregation(tagRequest);
                    int propagated = aggregationResponse.elements.size();
                    tagRequest.propagated = propagated;
                    LOGGER.trace("Arlas aggregation request returned {} hits with processtime={}ms", propagated, (System.currentTimeMillis() - t0));
                    for (int i = 0; i < propagated; i++) {
                        t0 = System.currentTimeMillis();
                        AggregationResponse a = aggregationResponse.elements.get(i);
                        Filter filter = getFilter(tagRequest.propagation.filter);

                        MultiValueFilter<Expression> expression =
                                new MultiValueFilter<>(new Expression(tagRequest.propagation.field,
                                        OperatorEnum.eq, a.key.toString()));
                        if (filter.f != null) {
                            filter.f.add(expression);
                        } else {
                            filter.f = List.of(expression);
                        }

                        Search search = new Search();
                        search.filter = filter;
                        tagKafkaProducer.sendToExecuteTags(a.key.toString(), TagRefRequest.fromTagRefRequest(tagRequest, search, propagated));
                        LOGGER.trace("Sent to Kafka {}/{} with processtime={}ms and key={}", i+1, propagated, (System.currentTimeMillis() - t0), a.key.toString());
                    }
                }
                updateStatus(tagRequest);
            } catch (IOException e) {
                LOGGER.warn("Could not parse record " + record.value());
            } catch (NotFoundException e) {
                LOGGER.warn("Could not get collection: " + record.value());
            } catch (NotAllowedException e) {
                LOGGER.warn("The path string is not part of the fields that can be tagged. " + record.value());
            } catch (InvalidParameterException e) {
                LOGGER.warn("Invalid parameters for request " + record.value());
            } catch (ArlasException e) {
                LOGGER.warn("Arlas exception for " + record.value(), e);
            }
        }
        LOGGER.debug("Finished processing {} tagref records with processtime={}ms", records.count(), (System.currentTimeMillis() - start));
    }

    private void updateStatus(TagRefRequest tagRequest) {
        UpdateResponse updateResponse = new UpdateResponse();
        updateResponse.id = tagRequest.id;
        updateResponse.action = tagRequest.action;
        updateResponse.label = tagRequest.label;
        updateResponse.propagated = tagRequest.propagated;
        TaggingStatus.getInstance().updateStatus(tagRequest, updateResponse, false, statusTimeout);
    }

    private Filter getFilter(Filter inFilter) {
        Filter outFilter = new Filter();
        if (inFilter != null) {
            if (inFilter.f != null) {
                outFilter.f = new ArrayList<>();
                Collections.copy(inFilter.f, outFilter.f);
            }
            outFilter.q = inFilter.q;
            outFilter.dateformat = inFilter.dateformat;
        }
        return outFilter;
    }

    private AggregationResponse getArlasAggregation(final TagRefRequest tagRequest) throws ArlasException, IOException {
        CollectionReference collectionReference = Optional
                .ofNullable(updateServices.getCollectionReferenceService().getCollectionReference(tagRequest.collection, Optional.empty()))
                .orElseThrow(() -> new NotFoundException(tagRequest.collection));

        Aggregation aggregation = new Aggregation();
        aggregation.type = AggregationTypeEnum.term;
        aggregation.field = tagRequest.propagation.field;
        aggregation.size = "10000";
        AggregationsRequest aggregationsRequest = new AggregationsRequest();
        aggregationsRequest.filter = tagRequest.search.filter;
        aggregationsRequest.aggregations = new ArrayList<>(List.of(aggregation));

        AggregationsRequest aggregationsRequestHeader = new AggregationsRequest();
        aggregationsRequestHeader.partitionFilter = ParamsParser.getPartitionFilter(collectionReference, tagRequest.partitionFilter);

        MixedRequest request = new MixedRequest();
        request.basicRequest = aggregationsRequest;
        request.headerRequest = aggregationsRequestHeader;

        return updateServices.aggregate(request,
                collectionReference,
                false,
                ((AggregationsRequest) request.basicRequest).aggregations,
                0,
                System.nanoTime());
    }
}
