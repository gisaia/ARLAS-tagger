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

package io.arlas.tagger.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.arlas.tagger.app.ArlasTaggerConfiguration;
import io.arlas.tagger.model.request.TagRefRequest;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.UUID;

public class TagKafkaProducer extends KafkaProducer<String, String> {

    private final Logger LOGGER = LoggerFactory.getLogger(TagKafkaProducer.class);

    private static final ObjectMapper jacksonMapper = new ObjectMapper();
    private final String tagRefLogTopic;
    private final String executeTagsTopic;

    public TagKafkaProducer(Properties properties, String tagRefLogTopic, String executeTagsTopic) {
        super(properties);
        this.tagRefLogTopic = tagRefLogTopic;
        this.executeTagsTopic = executeTagsTopic;
    }

    public void sendToTagRefLog(TagRefRequest tagRequest) {
        send(tagRefLogTopic, tagRequest);
    }

    public void sendToExecuteTags(String id, TagRefRequest tagRequest) {
        send(executeTagsTopic, id, tagRequest);
    }

    private void send(String topic, TagRefRequest tagRequest) {
        send(topic, null, tagRequest);
    }

    private void send(String topic, String id, TagRefRequest tagRequest) {

        try {
            LOGGER.debug("Sending to Kafka on topic " + topic);
            ProducerRecord<String, String> producerRecord =
                    id == null ?
                            new ProducerRecord<>(topic, jacksonMapper.writeValueAsString(tagRequest)) :
                            new ProducerRecord<>(topic, id, jacksonMapper.writeValueAsString(tagRequest));
            this.send(producerRecord);
        } catch (JsonProcessingException e) {
            LOGGER.warn("Failed to send to producer: " + e.getMessage());
        }
    }

    public static TagKafkaProducer build(ArlasTaggerConfiguration configuration) {
        Properties kafkaProperties = new Properties();
        kafkaProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, configuration.kafkaConfiguration.bootstrapServers);
        kafkaProperties.put(ProducerConfig.CLIENT_ID_CONFIG, UUID.randomUUID().toString());
        kafkaProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        kafkaProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        kafkaProperties.putAll(configuration.kafkaConfiguration.getExtraPropertiesAsMap());

        return new TagKafkaProducer(kafkaProperties,
                configuration.kafkaConfiguration.tagRefLogTopic,
                configuration.kafkaConfiguration.executeTagsTopic);
    }
}
