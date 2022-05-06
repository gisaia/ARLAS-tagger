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

import io.restassured.RestAssured;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public abstract class AbstractTaggerTestContext {
    static Logger LOGGER = LoggerFactory.getLogger(AbstractTaggerTestContext.class);

    protected static String arlasTaggerPath;

    static {
        String arlasTaggerHost = Optional.ofNullable(System.getenv("ARLAS_TAGGER_HOST")).orElse("localhost");
        int arlasPort = Integer.parseInt(Optional.ofNullable(System.getenv("ARLAS_TAGGER_PORT")).orElse("19998"));
        LOGGER.info(arlasTaggerHost + ":" + arlasPort);
        String arlasTaggerPrefix = Optional.ofNullable(System.getenv("ARLAS_TAGGER_PREFIX")).orElse("/arlas_tagger");
        String arlasTaggerAppPath = Optional.ofNullable(System.getenv("ARLAS_TAGGER_APP_PATH")).orElse("/");
        if (arlasTaggerAppPath.endsWith("/"))
            arlasTaggerAppPath = arlasTaggerAppPath.substring(0, arlasTaggerAppPath.length() - 1);
        arlasTaggerPath = arlasTaggerAppPath + arlasTaggerPrefix;
        if (arlasTaggerAppPath.endsWith("//"))
            arlasTaggerPath = arlasTaggerPath.substring(0, arlasTaggerPath.length() - 1);
        if (!arlasTaggerAppPath.endsWith("/"))
            arlasTaggerPath = arlasTaggerPath + "/";
    }

    protected void setTaggerRestAssured() {
        String arlasTaggerHost = Optional.ofNullable(System.getenv("ARLAS_TAGGER_HOST")).orElse("localhost");
        int arlasTaggerPort = Integer.parseInt(Optional.ofNullable(System.getenv("ARLAS_TAGGER_PORT")).orElse("19998"));
        RestAssured.baseURI = "http://" + arlasTaggerHost;
        RestAssured.port = arlasTaggerPort;
        RestAssured.basePath = "";
    }
    protected void setServerRestAssured() {
        String arlasHost = Optional.ofNullable(System.getenv("ARLAS_HOST")).orElse("localhost");
        int arlasPort = Integer.parseInt(Optional.ofNullable(System.getenv("ARLAS_PORT")).orElse("9999"));
        RestAssured.baseURI = "http://" + arlasHost;
        RestAssured.port = arlasPort;
        RestAssured.basePath = "";
    }
    protected abstract String getTaggerUrlPath(String collection);

}