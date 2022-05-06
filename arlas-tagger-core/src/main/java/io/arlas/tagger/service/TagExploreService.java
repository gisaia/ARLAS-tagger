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

import com.fasterxml.jackson.databind.ObjectMapper;
import io.arlas.tagger.app.ArlasTaggerConfiguration;
import io.arlas.tagger.kafka.TagKafkaConsumer;
import io.arlas.tagger.model.request.TagRefRequest;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class TagExploreService {
    private final Logger LOGGER = LoggerFactory.getLogger(TagExploreService.class);
    private final ArlasTaggerConfiguration configuration;
    private final String topic;
    private final String consumerGroupId;
    private final Integer batchSize;
    protected static ObjectMapper MAPPER = new ObjectMapper();

    public TagExploreService(ArlasTaggerConfiguration configuration, String topic, String consumerGroupId, Integer batchSize) {
        this.configuration = configuration;
        this.topic = topic;
        this.consumerGroupId =  consumerGroupId;
        this.batchSize = batchSize;
    }

    public List<TagRefRequest> getTagRefList() {
        List<TagRefRequest> results = new ArrayList<>();
        long threadId = Thread.currentThread().getId();
        try (KafkaConsumer<String, String> consumer = TagKafkaConsumer.build(configuration, topic, consumerGroupId+"-"+threadId, batchSize, true)) {
            consumer.seekToBeginning(consumer.assignment());
            ConsumerRecords<String, String> records;
            while (!(records = consumer.poll(Duration.ofMillis(configuration.kafkaConfiguration.consumerPollTimeout))).isEmpty()) {
                for (ConsumerRecord<String, String> record : records) {

                    try {
                        TagRefRequest tr = MAPPER.readValue(record.value(), TagRefRequest.class);
                        tr.offset = record.offset();
                        results.add(tr);
                    } catch (IOException e) {
                        LOGGER.warn("Could not parse record (ignored) " + record.value());
                    }

                }
            }
        }
        return results;
    }
}
