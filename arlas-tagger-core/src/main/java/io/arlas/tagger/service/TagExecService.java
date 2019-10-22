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
    private TaggingStatus taggingStatus;
    private Long statusTimeout;
    private int nbThread;


    public TagExecService(int nbThread, ArlasTaggerConfiguration configuration, String topic, String consumerGroupId, UpdateServices updateServices) {
        super(nbThread, configuration, topic, consumerGroupId, configuration.kafkaConfiguration.batchSizeTagExec);
        this.updateServices = updateServices;
        this.taggingStatus = TaggingStatus.getInstance();
        this.statusTimeout = configuration.statusTimeout;
    }

    @Override
    public void processRecords(ConsumerRecords<String, String> records) {
        long start = System.currentTimeMillis();
        long updatedTotal = 0l;
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
                UpdateResponse updResp = null;
                switch (tagRequest.action) {
                    case ADD:
                        updResp = updateServices.tag(collectionReference, request, tagRequest.tag, Integer.MAX_VALUE);
                        updatedTotal += updateStatus(tagRequest, updResp, t0);
                        break;
                    case REMOVE:
                        updResp = updateServices.unTag(collectionReference, request, tagRequest.tag, Integer.MAX_VALUE);
                        updatedTotal += updateStatus(tagRequest, updResp, t0);
                       break;
                    case REMOVEALL:
                        updResp = updateServices.removeAll(collectionReference, request, tagRequest.tag, Integer.MAX_VALUE);
                        updatedTotal += updateStatus(tagRequest, updResp, t0);
                        break;
                    default:
                        LOGGER.warn("Unknown action received in tag request: " + tagRequest.action);
                        break;
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

    private long updateStatus(TagRefRequest tagRequest, UpdateResponse updResp, long t0) {
        UpdateResponse updateResponse = taggingStatus.getStatus(tagRequest.id).orElse(new UpdateResponse());
        updateResponse.id = tagRequest.id;
        updateResponse.nbResult = tagRequest.nbResult;
        updateResponse.action = tagRequest.action;
        updateResponse.add(updResp);
        taggingStatus.updateStatus(tagRequest.id, updateResponse, statusTimeout);
        LOGGER.trace("Tagged {} documents [total={} / {}%] (failed={}) with processtime={}ms", updResp.updated, updateResponse.updated, updateResponse.progress, updateResponse.failed, (System.currentTimeMillis() - t0));
        return updResp.updated;
    }
}
