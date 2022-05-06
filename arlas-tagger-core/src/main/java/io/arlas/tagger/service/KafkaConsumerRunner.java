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
import io.arlas.tagger.model.TaggingStatus;
import org.apache.kafka.clients.consumer.CommitFailedException;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class KafkaConsumerRunner implements Runnable {
    Logger LOGGER = LoggerFactory.getLogger(KafkaConsumerRunner.class);

    private final ArlasTaggerConfiguration configuration;
    protected final String topic;
    private final String consumerGroupId;
    private final Integer batchSize;
    private final int nbThread;
    private final AtomicBoolean closed = new AtomicBoolean(false);
    private KafkaConsumer consumer;
    protected static ObjectMapper MAPPER = new ObjectMapper();
    volatile protected long replayFromOffset = -1L;

    public KafkaConsumerRunner(int nbThread, ArlasTaggerConfiguration configuration, String topic, String consumerGroupId, Integer batchSize) {
        this.configuration = configuration;
        this.topic = topic;
        this.consumerGroupId = consumerGroupId;
        this.batchSize = batchSize;
        this.nbThread = nbThread;
    }

    public abstract void processRecords(ConsumerRecords<String, String> records);

    public abstract void setReplayFromOffset(long replayFromOffset);

    @Override
    public void run() {
        try {
            LOGGER.info("[{}-{}] Starting consumer", topic, nbThread);
            consumer = TagKafkaConsumer.build(configuration, topic, consumerGroupId, batchSize);
            long start = System.currentTimeMillis();
            long duration;
            int nbFailure = 0;

            while (true) {
                try {
                    if (replayFromOffset != -1L) {
                        // replay is only possible when working with 1 partition
                        Long maxOffset= (Long) consumer.endOffsets(consumer.assignment()).values().toArray()[0];
                        if (replayFromOffset <= maxOffset) {
                            consumer.seek((TopicPartition) (consumer.assignment().toArray()[0]), replayFromOffset);
                            // resetting all past status information else we can't get the new status
                            TaggingStatus.getInstance().reset();
                        } else {
                            LOGGER.warn("Ignoring attempt of replay from offset " + replayFromOffset + " because it is larger than max offset " + maxOffset);
                        }
                        replayFromOffset = -1L;
                    }
                    ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(configuration.kafkaConfiguration.consumerPollTimeout));
                    if (records.count() > 0) {
                        LOGGER.debug("[{}-{}] Nb records polled={}", topic, nbThread, records.count());
                        start = System.currentTimeMillis();
                        processRecords(records);
                        consumer.commitSync();
                        nbFailure = 0;
                    }
                } catch (CommitFailedException e) {
                    nbFailure++;
                    duration = System.currentTimeMillis() - start;
                    LOGGER.warn("[{}-{}] Commit failed (attempt nb {}): process time={}ms (compare to max.poll.interval.ms value) / exception={}", topic, nbThread, nbFailure, duration, e.getMessage());
                    if (nbFailure > configuration.kafkaConfiguration.commitMaxRetries) {
                        LOGGER.error("[{}-{}] Too many attempts, exiting.", topic, nbThread);
                        try { consumer.close(); } catch (RuntimeException ignored) {}
                        System.exit(1);
                    }
                }
            }
        } catch (WakeupException e) {
            // Ignore exception if closing
            if (!closed.get()) throw e;
        } finally {
            LOGGER.info("[{}-{}] Closing consumer", topic, nbThread);
            try { consumer.close(); } catch (RuntimeException ignored) {}
        }
    }

    // Shutdown hook which can be called from a separate thread
    public void stop() {
        closed.set(true);
        consumer.wakeup();
    }
}
