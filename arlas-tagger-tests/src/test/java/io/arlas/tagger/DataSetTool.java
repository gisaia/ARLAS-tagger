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
import io.arlas.tagger.app.ElasticConfiguration;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.core.util.IOUtils;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.AdminClient;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.List;
import java.util.Optional;

public class DataSetTool {
    static Logger LOGGER = LoggerFactory.getLogger(DataSetTool.class);

    public final static String DATASET_INDEX_NAME="dataset";
    public final static String DATASET_TYPE_NAME="mytype";
    public final static String DATASET_ID_PATH="id";
    public final static String DATASET_GEOMETRY_PATH="geo_params.geometry";
    public final static String DATASET_CENTROID_PATH="geo_params.centroid";
    public final static String DATASET_TIMESTAMP_PATH="params.startdate";
    public final static String DATASET_TAGGABLE_FIELDS="params.tags,params.job";
    public static final String[] jobs = {"Actor", "Announcers", "Archeologists", "Architect", "Brain Scientist", "Chemist", "Coach", "Coder", "Cost Estimator", "Dancer", "Drafter"};

    public static AdminClient adminClient;
    public static Client client;

    static {
        try {
            Settings settings = null;
            List<Pair<String,Integer>> nodes = ElasticConfiguration.getElasticNodes(Optional.ofNullable(System.getenv("ARLAS_ELASTIC_NODES")).orElse("localhost:19300"));
            if ("localhost".equals(nodes.get(0).getLeft())) {
                settings = Settings.EMPTY;
            } else {
                settings = Settings.builder().put("cluster.name", "docker-cluster").build();
            }
            client = new PreBuiltTransportClient(settings)
                    .addTransportAddress(new TransportAddress(InetAddress.getByName(nodes.get(0).getLeft()), nodes.get(0).getRight()));
            adminClient = client.admin();
            LOGGER.info("Load data in " + nodes.get(0).getLeft() + ":" + nodes.get(0).getRight());
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public static void main(String[] args) throws IOException {
        DataSetTool.loadDataSet();
    }

    public static void loadDataSet() throws IOException {
        createIndex(DATASET_INDEX_NAME,"dataset.mapping.json");
        fillIndex(DATASET_INDEX_NAME,-170,170,-80,80);
        LOGGER.info("Index created : " + DATASET_INDEX_NAME);
    }

    private static void createIndex(String indexName, String mappingFileName) throws IOException {
        String mapping = IOUtils.toString(new InputStreamReader(DataSetTool.class.getClassLoader().getResourceAsStream(mappingFileName)));
        try {
            adminClient.indices().prepareDelete(indexName).get();
        } catch (Exception e) {
        }
        adminClient.indices().prepareCreate(indexName).addMapping(DATASET_TYPE_NAME, mapping, XContentType.JSON).get();
    }

    private static void fillIndex(String indexName, int lonMin, int lonMax, int latMin, int latMax) throws JsonProcessingException {
        Data data;
        ObjectMapper mapper = new ObjectMapper();

        for (int i = lonMin; i <= lonMax; i += 10) {
            for (int j = latMin; j <= latMax; j += 10) {
                data = new Data();
                data.id = String.valueOf("ID_" + i + "_" + j + "DI").replace("-", "_");
                data.params.job = jobs[((Math.abs(i) + Math.abs(j)) / 10) % (jobs.length - 1)];
                IndexResponse response = client.prepareIndex(indexName, DATASET_TYPE_NAME, "ES_ID_TEST" + data.id)
                        .setSource(mapper.writer().writeValueAsString(data), XContentType.JSON)
                        .get();
            }
        }
    }

    public static void clearDataSet() {
        adminClient.indices().prepareDelete(DATASET_INDEX_NAME).get();
    }

    public static void close() {
        client.close();
    }
}
