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

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.arlas.server.exceptions.*;
import io.arlas.server.managers.CollectionReferenceManager;
import io.arlas.server.utils.ElasticNodesInfo;
import io.arlas.server.utils.InsensitiveCaseFilter;
import io.arlas.server.utils.PrettyPrintFilter;
import io.arlas.tagger.kafka.TagKafkaProducer;
import io.arlas.tagger.rest.tag.TagRESTService;
import io.arlas.tagger.rest.tag.TagStatusRESTService;
import io.arlas.tagger.service.ManagedKafkaConsumers;
import io.arlas.tagger.service.TagExploreService;
import io.arlas.tagger.service.UpdateServices;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.federecio.dropwizard.swagger.SwaggerBundle;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.elasticsearch.xpack.client.PreBuiltXPackTransportClient;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.EnumSet;

public class ArlasTagger extends Application<ArlasTaggerConfiguration> {
    Logger LOGGER = LoggerFactory.getLogger(ArlasTagger.class);

    public static void main(String... args) throws Exception {
        new ArlasTagger().run(args);
    }

    @Override
    public void initialize(Bootstrap<ArlasTaggerConfiguration> bootstrap) {
        bootstrap.registerMetrics();
        bootstrap.setConfigurationSourceProvider(new SubstitutingSourceProvider(
                bootstrap.getConfigurationSourceProvider(), new EnvironmentVariableSubstitutor(false)));
        bootstrap.addBundle(new SwaggerBundle<ArlasTaggerConfiguration>() {
            @Override
            protected SwaggerBundleConfiguration getSwaggerBundleConfiguration(ArlasTaggerConfiguration configuration) {
                return configuration.swaggerBundleConfiguration;
            }
        });
        bootstrap.addBundle(new AssetsBundle("/assets/", "/", "index.html"));
    }

    @Override
    public void run(ArlasTaggerConfiguration configuration, Environment environment) throws Exception {
        configuration.check();

        TransportClient transportClient = getTransportClient(configuration);
        Client client = transportClient;

        CollectionReferenceManager.getInstance().init(client);

        UpdateServices updateServices = new UpdateServices(client, configuration.arlasCollectionsConfiguration);
        TagKafkaProducer tagKafkaProducer = TagKafkaProducer.build(configuration);
        ManagedKafkaConsumers consumersManager = new ManagedKafkaConsumers(configuration, tagKafkaProducer, updateServices);
        environment.lifecycle().manage(consumersManager);
        TagExploreService tagExploreService = new TagExploreService(configuration,
                configuration.kafkaConfiguration.tagRefLogTopic,
                configuration.kafkaConfiguration.exploreTagsConsumerGroupId,
                configuration.kafkaConfiguration.batchSizeTagRef);

        environment.getObjectMapper().setSerializationInclusion(Include.NON_NULL);
        environment.getObjectMapper().configure(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS, false);
        environment.jersey().register(MultiPartFeature.class);
        environment.jersey().register(new ArlasExceptionMapper());
        environment.jersey().register(new IllegalArgumentExceptionMapper());
        environment.jersey().register(new JsonProcessingExceptionMapper());
        environment.jersey().register(new ConstraintViolationExceptionMapper());
        environment.jersey().register(new ElasticsearchExceptionMapper());
        environment.jersey().register(new TagRESTService(tagKafkaProducer, configuration.statusTimeout));
        environment.jersey().register(new TagStatusRESTService(tagExploreService));

        //filters
        environment.jersey().register(PrettyPrintFilter.class);
        environment.jersey().register(InsensitiveCaseFilter.class);

        //cors
        if (configuration.arlascorsenabled) {
            configureCors(environment);
        }

        ElasticNodesInfo.printNodesInfo(client, transportClient);
    }

    private void configureCors(Environment environment) {
        CrossOriginFilter filter = new CrossOriginFilter();
        final FilterRegistration.Dynamic cors = environment.servlets().addFilter("CrossOriginFilter", filter);

        // Configure CORS parameters
        cors.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
        cors.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM, "X-Requested-With,Content-Type,Accept,Origin,Authorization");
        cors.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "OPTIONS,GET,PUT,POST,DELETE,HEAD");
        cors.setInitParameter(CrossOriginFilter.ALLOW_CREDENTIALS_PARAM, "true");
        //cors.setInitParameter(CrossOriginFilter.PREFLIGHT_MAX_AGE_PARAM, "");
        cors.setInitParameter(CrossOriginFilter.EXPOSED_HEADERS_PARAM, "Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin,Location");

        // Add URL mapping
        cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
    }

    protected TransportClient getTransportClient(ArlasTaggerConfiguration configuration) throws UnknownHostException {
        TransportClient transportClient;
        // x-pack transport client
        if (configuration.elasticConfiguration.elasticEnableSsl) {
            // disable JVM default policies of caching positive hostname resolutions indefinitely
            // because the Elastic load balancer can change IP addresses
            java.security.Security.setProperty("networkaddress.cache.ttl" , "60");
            // Build the settings for our client.
            Settings settings = Settings.builder()
                    .put("cluster.name", configuration.elasticConfiguration.elasticcluster)
                    .put("request.headers.X-Found-Cluster", "${cluster.name}")
                    .put("client.transport.sniff", false) // must be false in this context
                    .put("transport.compress", configuration.elasticConfiguration.elasticCompress) // from ES 6.7
                    .put("xpack.security.transport.ssl.enabled", configuration.elasticConfiguration.elasticEnableSsl)
                    .put("xpack.security.user", configuration.elasticConfiguration.elasticCredentials)
                    .build();

            // Instantiate a TransportClient and add the cluster to the list of addresses to connect to.
            // Only port 9343 (SSL-encrypted) is currently supported. The use of x-pack security features is required.
            transportClient = new PreBuiltXPackTransportClient(settings);
        } else {
            Settings.Builder settingsBuilder = Settings.builder();
            if(configuration.elasticConfiguration.elasticsniffing) {
                settingsBuilder.put("client.transport.sniff", true);
            }
            if(!Strings.isNullOrEmpty(configuration.elasticConfiguration.elasticcluster)) {
                settingsBuilder.put("cluster.name", configuration.elasticConfiguration.elasticcluster);
            }
            Settings settings = settingsBuilder.build();


            transportClient = new PreBuiltTransportClient(settings);
        }

        for(Pair<String,Integer> node : configuration.elasticConfiguration.getElasticNodes()) {
            transportClient.addTransportAddress(new TransportAddress(InetAddress.getByName(node.getLeft()),
                    node.getRight()));
        }
        return transportClient;
    }
}
