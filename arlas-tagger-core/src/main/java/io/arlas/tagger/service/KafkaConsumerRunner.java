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
import org.apache.kafka.clients.consumer.CommitFailedException;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class KafkaConsumerRunner implements Runnable {
    Logger LOGGER = LoggerFactory.getLogger(KafkaConsumerRunner.class);

    private final ArlasTaggerConfiguration configuration;
    private final String topic;
    private final String consumerGroupId;
    private final Integer batchSize;
    private final AtomicBoolean closed = new AtomicBoolean(false);
    private KafkaConsumer consumer;
    protected static ObjectMapper MAPPER = new ObjectMapper();

    public KafkaConsumerRunner(ArlasTaggerConfiguration configuration, String topic, String consumerGroupId, Integer batchSize) {
        this.configuration = configuration;
        this.topic = topic;
        this.consumerGroupId = consumerGroupId;
        this.batchSize = batchSize;
    }

    public abstract void processRecords(ConsumerRecords<String, String> records);

    @Override
    public void run() {
        try {
            LOGGER.info("["+topic+"] Starting consumer");
            consumer = TagKafkaConsumer.build(configuration, topic, consumerGroupId, batchSize);
            long start = System.currentTimeMillis();
            long duration = System.currentTimeMillis();
            int nbFailure = 0;

            while (true) {
                try {
                    ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(configuration.kafkaConfiguration.consumerPollTimeout));
                    if (records.count() > 0) {
                        LOGGER.debug("["+topic+"] Nb records polled=" + records.count());
                        start = System.currentTimeMillis();
                        processRecords(records);
                        consumer.commitSync();
                        nbFailure = 0;
                    }
                } catch (CommitFailedException e) {
                    nbFailure++;
                    duration = System.currentTimeMillis() - start;
                    LOGGER.warn("["+topic+"] Commit failed (attempt nb " + nbFailure + "): process time=" + duration + "ms (compare to max.poll.interval.ms value) / exception=" + e.getMessage());
                    if (nbFailure > configuration.kafkaConfiguration.commitMaxRetries) {
                        LOGGER.error("["+topic+"] Too many attempts, exiting.");
                        try { consumer.close(); } catch (RuntimeException r) {}
                        System.exit(1);
                    }
                }
            }
        } catch (WakeupException e) {
            // Ignore exception if closing
            if (!closed.get()) throw e;
        } finally {
            LOGGER.info("["+topic+"] Closing consumer");
            try { consumer.close(); } catch (RuntimeException r) {}
        }
    }

    // Shutdown hook which can be called from a separate thread
    public void stop() {
        closed.set(true);
        consumer.wakeup();
    }
}
