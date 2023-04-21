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

package io.arlas.tagger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.arlas.commons.exceptions.ArlasException;
import io.arlas.server.core.app.ElasticConfiguration;
import io.arlas.server.core.impl.elastic.utils.ElasticClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;

public class DataSetTool {
    static Logger LOGGER = LoggerFactory.getLogger(DataSetTool.class);

    public final static String DATASET_INDEX_NAME="dataset";
    public final static String DATASET_ID_PATH="id";
    public final static String DATASET_GEOMETRY_PATH="geo_params.geometry";
    public final static String DATASET_CENTROID_PATH="geo_params.centroid";
    public final static String DATASET_TIMESTAMP_PATH="params.startdate";
    public final static String DATASET_TAGGABLE_FIELDS="params.tags,params.job";
    public final static String DATASET_H3_PATH="geo_params.h3";

    public static final String[] jobs = {"Actor", "Announcers", "Archeologists", "Architect", "Brain Scientist", "Chemist", "Coach", "Coder", "Cost Estimator", "Dancer", "Drafter"};

    public static ElasticClient client;

    static {
        ElasticConfiguration conf = new ElasticConfiguration();
        conf.elasticnodes = Optional.ofNullable(System.getenv("ARLAS_ELASTIC_NODES")).orElse("localhost:9200");
        conf.elasticEnableSsl = false;
        conf.elasticSocketTimeout = 30000;
        client = new ElasticClient(conf);
        LOGGER.info("Load data in " + conf.elasticnodes);

    }

    public static void main(String[] args) throws IOException, ArlasException {
        DataSetTool.loadDataSet();
    }

    public static void loadDataSet() throws IOException, ArlasException {
        createIndex(DATASET_INDEX_NAME,"dataset.mapping.json");
        fillIndex(DATASET_INDEX_NAME,-170,170,-80,80);
        LOGGER.info("Index created : " + DATASET_INDEX_NAME);
    }

    private static void createIndex(String indexName, String mappingFileName) throws IOException, ArlasException {
        try {
            client.deleteIndex(indexName);
        } catch (Exception ignored) {
        }
        client.getClient().indices().create(b -> b.index(indexName).withJson(new InputStreamReader(DataSetTool.class.getClassLoader().getResourceAsStream(mappingFileName))));
    }

    private static void fillIndex(String indexName, int lonMin, int lonMax, int latMin, int latMax) throws JsonProcessingException, ArlasException {
        Data data;
        ObjectMapper mapper = new ObjectMapper();

        for (int i = lonMin; i <= lonMax; i += 10) {
            for (int j = latMin; j <= latMax; j += 10) {
                data = new Data();
                data.id = ("ID_" + i + "_" + j + "DI").replace("-", "_");
                data.params.job = jobs[((Math.abs(i) + Math.abs(j)) / 10) % (jobs.length - 1)];
                client.index(indexName, "ES_ID_TEST" + data.id, data);
            }
        }
    }

    public static void clearDataSet() throws ArlasException {
        client.deleteIndex(DATASET_INDEX_NAME);
    }

    public static void close() throws IOException {
        client.getClient()._transport().close();
    }
}
