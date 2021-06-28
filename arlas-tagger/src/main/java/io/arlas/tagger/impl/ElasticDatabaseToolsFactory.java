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
package io.arlas.tagger.impl;

import com.codahale.metrics.health.HealthCheck;
import io.arlas.server.admin.health.ElasticsearchHealthCheck;
import io.arlas.server.core.impl.elastic.services.ElasticCollectionReferenceService;
import io.arlas.server.core.impl.elastic.utils.ElasticClient;
import io.arlas.server.core.managers.CacheManager;
import io.arlas.server.core.services.CollectionReferenceService;
import io.arlas.tagger.app.ArlasTaggerConfiguration;
import io.arlas.tagger.app.DatabaseToolsFactory;
import io.arlas.tagger.service.UpdateServices;

import java.util.HashMap;
import java.util.Map;


public class ElasticDatabaseToolsFactory extends DatabaseToolsFactory {
    private final ElasticClient elasticClient;
    private final UpdateServices updateServices;
    private final CollectionReferenceService collectionReferenceService;

    public ElasticDatabaseToolsFactory(ArlasTaggerConfiguration configuration, CacheManager cacheManager) {
        super(configuration);
        this.elasticClient = new ElasticClient(configuration.elasticConfiguration);
        this.collectionReferenceService = new ElasticCollectionReferenceService(elasticClient,
                configuration.arlasCollectionsConfiguration.arlasIndex, cacheManager);
        this.updateServices = new UpdateServices(elasticClient,
                collectionReferenceService,
                configuration.arlasRestCacheTimeout);
    }

    @Override
    public UpdateServices getUpdateServices() {
        return this.updateServices;
    }

    @Override
    public CollectionReferenceService getCollectionReferenceService() {
        return this.collectionReferenceService;
    }

    @Override
    public Map<String, HealthCheck> getHealthChecks() {
        Map<String, HealthCheck> ret = new HashMap<>();
        ret.put("elasticsearch", new ElasticsearchHealthCheck(elasticClient));
        return ret;
    }
}
