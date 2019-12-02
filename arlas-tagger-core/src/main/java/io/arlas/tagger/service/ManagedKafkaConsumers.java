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

import io.arlas.tagger.app.ArlasTaggerConfiguration;
import io.arlas.tagger.kafka.TagKafkaProducer;
import io.dropwizard.lifecycle.Managed;

import java.util.ArrayList;
import java.util.List;

public class ManagedKafkaConsumers implements Managed {
    private ArlasTaggerConfiguration configuration;
    private TagRefService tagRefService;
    private List<TagExecService> tagExecServices;
    private UpdateServices updateServices;
    private TagKafkaProducer tagKafkaProducer;
    
    public ManagedKafkaConsumers(ArlasTaggerConfiguration configuration, TagKafkaProducer tagKafkaProducer, UpdateServices updateServices) {
        this.configuration = configuration;
        this.tagKafkaProducer = tagKafkaProducer;
        this.tagRefService = new TagRefService(configuration,
                configuration.kafkaConfiguration.tagRefLogTopic,
                configuration.kafkaConfiguration.tagRefLogConsumerGroupId,
                tagKafkaProducer, updateServices);

        this.tagExecServices = new ArrayList<>();
        this.updateServices = updateServices;
    }

    public TagKafkaProducer getTagKafkaProducer() {
        return tagKafkaProducer;
    }

    @Override
    public void start() throws Exception {
        new Thread(tagRefService).start();
        for (int i=0; i < configuration.kafkaConfiguration.nbTagExec; i++){
            TagExecService t = new TagExecService(i, configuration,
                    configuration.kafkaConfiguration.executeTagsTopic,
                    configuration.kafkaConfiguration.executeTagsConsumerGroupId,
                    updateServices);
            tagExecServices.add(t);
            new Thread(t).start();
        }
    }

    @Override
    public void stop() throws Exception {
        this.tagRefService.stop();
        for (int i=0; i < tagExecServices.size(); i++) {
            try { this.tagExecServices.get(i).stop(); } catch (Exception e) {};
        }
    }

    public void replayFrom(long offset) {
        tagRefService.setReplayFromOffset(offset);
    }
}
