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
import io.arlas.server.app.ElasticConfiguration;
import io.arlas.server.exceptions.ArlasConfigurationException;
import io.dropwizard.Configuration;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;

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
    public Boolean arlasCorsEnabled;

    @JsonProperty("arlas_rest_cache_timeout")
    public int arlasRestCacheTimeout;

    @JsonProperty("tagging_status_timeout")
    public Long statusTimeout;

    @JsonProperty("arlas_database_factory_class")
    public String arlasDatabaseFactoryClass;

    public void check() throws ArlasConfigurationException {
        elasticConfiguration.check();
        if (arlasAuthConfiguration == null) {
            arlasAuthConfiguration = new ArlasAuthConfiguration();
            arlasAuthConfiguration.enabled = false;
        }

        if (arlasDatabaseFactoryClass == null) {
            throw new ArlasConfigurationException("arlas_database_factory_class is missing");
        }
    }
}
