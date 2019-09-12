/*
 * Licensed to Gisaïa under one or more contributor
 * license agreements. See the NOTICE.txt file distributed with
 * this work for additional information regarding copyright
 * ownership. Gisaïa licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE_2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.arlas.tagger.app;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.arlas.tagger.util.StringToMap;

import java.util.Map;

public class KafkaConfiguration {
    @JsonProperty("status_timeout")
    public Long statusTimeout;

    @JsonProperty("kafka_batch_size")
    public Integer batchSize;

    @JsonProperty("kafka_bootstrap_servers")
    public String bootstrapServers;

    @JsonProperty("kafka_consumer_poll_timeout")
    public Long consumerPollTimeout;

    @JsonProperty("kafka_consumer_group_id_tagref_log")
    public String tagRefLogConsumerGroupId;

    @JsonProperty("kafka_consumer_group_id_execute_tags")
    public String executeTagsConsumerGroupId;

    @JsonProperty("kafka_topic_tagref_log")
    public String tagRefLogTopic;

    @JsonProperty("kafka_topic_execute_tags")
    public String executeTagsTopic;

    @JsonProperty("kafka_extra_properties")
    public String kafkaExtraProperties;

    public Map<String, String> getExtraPropertiesAsMap() {
        return StringToMap.parse(kafkaExtraProperties);
    }
}
