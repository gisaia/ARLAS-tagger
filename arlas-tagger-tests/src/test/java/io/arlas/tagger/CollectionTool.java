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

import com.fasterxml.jackson.databind.ObjectMapper;
import io.arlas.commons.exceptions.ArlasException;
import io.arlas.server.core.model.CollectionReferenceParameters;
import io.arlas.server.core.model.DublinCoreElementName;
import io.arlas.server.core.model.enumerations.OperatorEnum;
import io.arlas.server.core.model.request.Expression;
import io.arlas.server.core.model.request.Filter;
import io.arlas.server.core.model.request.MultiValueFilter;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;

public class CollectionTool extends ArlasServerContext {

        public static String COLLECTION_NAME = "geodata";
        public static String COLLECTION_NAME_ACTOR = "geodata_actor";

        public static void main(String[] args) throws ArlasException, IOException {
            switch (args[0]) {
                case "load" -> new CollectionTool().load();
                case "delete" -> new CollectionTool().delete();
            }
            DataSetTool.close();
        }

        @Test
        public  void load() {
            this.load(0);
        }

        public  void load(long sleepAfter) {

            try {
                DataSetTool.loadDataSet();
            } catch (IOException | ArlasException e) {
                e.printStackTrace();
            }

            CollectionReferenceParameters params = new CollectionReferenceParameters();
            params.indexName = DataSetTool.DATASET_INDEX_NAME;
            params.idPath = DataSetTool.DATASET_ID_PATH;
            params.geometryPath = DataSetTool.DATASET_GEOMETRY_PATH;
            params.centroidPath = DataSetTool.DATASET_CENTROID_PATH;
            params.timestampPath = DataSetTool.DATASET_TIMESTAMP_PATH;
            params.taggableFields = DataSetTool.DATASET_TAGGABLE_FIELDS;

            // PUT new collection
            given().contentType("application/json").body(params).when().put(getUrlPath()).then().statusCode(200);
            Filter filter = new Filter();
            filter.f = List.of(new MultiValueFilter<>(new Expression("params.job", OperatorEnum.eq, DataSetTool.jobs[0])));
            params.filter =filter;
            given().contentType("application/json").body(params).when().put(arlasPath + "collections/" + COLLECTION_NAME_ACTOR).then().statusCode(200);

            try {
                Thread.sleep(sleepAfter);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Test
        public  void loadCsw() throws IOException {
            this.loadCsw(0);
        }

        public  void loadCsw(long sleepAfter) throws IOException {
            try {
                DataSetTool.loadDataSet();
            } catch (IOException | ArlasException e) {
                e.printStackTrace();
            }

            InputStreamReader dcelementForCollection = new InputStreamReader(CollectionTool.class.getClassLoader().getResourceAsStream("csw.collection.dcelements.json"));
            ObjectMapper objectMapper = new ObjectMapper();
            DublinCoreElementName[] dcelements = objectMapper.readValue(dcelementForCollection, DublinCoreElementName[].class);
            Arrays.asList(dcelements).forEach(dublinCoreElementName -> {
                        CollectionReferenceParameters params = new CollectionReferenceParameters();
                        params.indexName = DataSetTool.DATASET_INDEX_NAME;
                        params.idPath = DataSetTool.DATASET_ID_PATH;
                        params.geometryPath = DataSetTool.DATASET_GEOMETRY_PATH;
                        params.centroidPath = DataSetTool.DATASET_CENTROID_PATH;
                        params.timestampPath = DataSetTool.DATASET_TIMESTAMP_PATH;
                        params.taggableFields = DataSetTool.DATASET_TAGGABLE_FIELDS;
                        params.dublinCoreElementName=dublinCoreElementName;
                        String url = arlasPath + "collections/" + dublinCoreElementName.title.split(" ")[0].toLowerCase();
                        // PUT new collection
                        given().contentType("application/json").body(params).when().put(url).then().statusCode(200);
                    }
            );
            try {
                Thread.sleep(sleepAfter);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public  void delete() throws ArlasException {
            DataSetTool.clearDataSet();
            //DELETE collection
            when().delete(getUrlPath()).then().statusCode(200);
            when().delete(arlasPath + "collections/" + COLLECTION_NAME_ACTOR).then().statusCode(200);
        }


        protected static String getUrlPath() {
            return arlasPath + "collections/" + COLLECTION_NAME;
        }

    }
