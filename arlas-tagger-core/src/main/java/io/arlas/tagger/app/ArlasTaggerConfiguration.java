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

package io.arlas.tagger.app;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.arlas.server.app.ArlasAuthConfiguration;
import io.arlas.server.exceptions.ArlasConfigurationException;
import io.dropwizard.Configuration;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;

import java.util.Map;

public class ArlasTaggerConfiguration extends Configuration {
    @JsonProperty("swagger")
    public SwaggerBundleConfiguration swaggerBundleConfiguration;

    @JsonProperty("elastic_configuration")
    public ElasticConfiguration elasticConfiguration;

    @JsonProperty("kafka_configuration")
    public KafkaConfiguration kafkaConfiguration;

    @JsonProperty("arlas_auth")
    public ArlasAuthConfiguration arlasAuthConfiguration;

    @JsonProperty("arlas_collections_configuration")
    public ArlasCollectionsConfiguration arlasCollectionsConfiguration;

    @JsonProperty("arlas_cors_enabled")
    public Boolean arlascorsenabled;

    @JsonProperty("tagging_status_timeout")
    public Long statusTimeout;

    public void check() throws ArlasConfigurationException {
        if (elasticConfiguration.getElasticNodes().isEmpty()) {
            throw new ArlasConfigurationException("Elastic search configuration missing in config file.");
        }
        if (elasticConfiguration.elasticEnableSsl == null) {
            elasticConfiguration.elasticEnableSsl = false;
        }
        if (elasticConfiguration.elasticCompress == null) {
            elasticConfiguration.elasticCompress = true;
        }
        if (arlasAuthConfiguration == null) {
            arlasAuthConfiguration = new ArlasAuthConfiguration();
            arlasAuthConfiguration.enabled = false;
        }
    }
}
