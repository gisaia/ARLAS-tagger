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

import io.arlas.server.exceptions.ArlasException;
import io.arlas.server.exceptions.InvalidParameterException;
import io.arlas.server.exceptions.NotAllowedException;
import io.arlas.server.model.CollectionReference;
import io.arlas.server.model.request.MixedRequest;
import io.arlas.server.model.request.Search;
import io.arlas.server.utils.ParamsParser;
import io.arlas.tagger.app.ArlasTaggerConfiguration;
import io.arlas.tagger.model.TaggingStatus;
import io.arlas.tagger.model.request.TagRefRequest;
import io.arlas.tagger.model.response.UpdateResponse;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.NotFoundException;
import java.io.IOException;
import java.util.Optional;

public class TagExecService extends KafkaConsumerRunner {
    private Logger LOGGER = LoggerFactory.getLogger(TagExecService.class);
    private UpdateServices updateServices;
    private Long statusTimeout;


    public TagExecService(int nbThread, ArlasTaggerConfiguration configuration, String topic, String consumerGroupId, UpdateServices updateServices) {
        super(nbThread, configuration, topic, consumerGroupId, configuration.kafkaConfiguration.batchSizeTagExec);
        this.updateServices = updateServices;
        this.statusTimeout = configuration.statusTimeout;
    }

    @Override
    public void setReplayFromOffset(long replayFromOffset) {
        throw new UnsupportedOperationException("Replay is not available on topic '" + topic + "' ");
    }

    @Override
    public void processRecords(ConsumerRecords<String, String> records) {
        long start = System.currentTimeMillis();
        long updatedTotal = 0l;
        int nbErrors = 0;
        for (ConsumerRecord<String, String> record : records) {
            try {
                long t0 = System.currentTimeMillis();
                final TagRefRequest tagRequest = MAPPER.readValue(record.value(), TagRefRequest.class);
                LOGGER.debug("Processing record {}", tagRequest.toString());
                CollectionReference collectionReference = Optional
                        .ofNullable(updateServices.getDaoCollectionReference().getCollectionReference(tagRequest.collection))
                        .orElseThrow(() -> new NotFoundException(tagRequest.collection));
                Search searchHeader = new Search();
                searchHeader.filter = ParamsParser.getFilter(tagRequest.partitionFilter);
                MixedRequest request = new MixedRequest();
                request.basicRequest = tagRequest.search;
                request.headerRequest = searchHeader;
                UpdateResponse opUpdateResponse = null;
                switch (tagRequest.action) {
                    case ADD:
                        opUpdateResponse = updateServices.tag(collectionReference, request, tagRequest.tag, Integer.MAX_VALUE);
                        break;
                    case REMOVE:
                        opUpdateResponse = updateServices.unTag(collectionReference, request, tagRequest.tag, Integer.MAX_VALUE);
                       break;
                    case REMOVEALL:
                        opUpdateResponse = updateServices.removeAll(collectionReference, request, tagRequest.tag, Integer.MAX_VALUE);
                        break;
                    default:
                        LOGGER.warn("Unknown action received in tag request: " + tagRequest.action);
                        break;
                }
                if (opUpdateResponse != null) {
                    updatedTotal += opUpdateResponse.updated;
                    UpdateResponse tagUpdateResponse = TaggingStatus.getInstance().updateStatus(tagRequest, opUpdateResponse, true, statusTimeout);
                    LOGGER.trace("Tagged {} documents [total={} / {}%] (failed={}) with processtime={}ms", opUpdateResponse.updated, tagUpdateResponse.updated, tagUpdateResponse.progress, tagUpdateResponse.failed, (System.currentTimeMillis() - t0));
                    if (tagUpdateResponse.failed > 0) {
                        LOGGER.error("Tagged {} documents [total={} / {}%] (failed={}) with processtime={}ms", opUpdateResponse.updated, tagUpdateResponse.updated, tagUpdateResponse.progress, tagUpdateResponse.failed, (System.currentTimeMillis() - t0));
                        int newErrors =  (int)tagUpdateResponse.failed - nbErrors;
                        for(int i = nbErrors; i < nbErrors + newErrors; i++) {
                            UpdateResponse.Failure f = tagUpdateResponse.failures.get(i);
                            LOGGER.error("Failure {}: \n - id : {} \n - type : {} \n - message : {}", i, f.id, f.type, f.message);
                        }
                        nbErrors += newErrors;
                    }
                }
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
        LOGGER.debug("Finished processing {} tagexec records ({} docs) with processtime={}ms", records.count(), updatedTotal, (System.currentTimeMillis() - start));
    }
}
