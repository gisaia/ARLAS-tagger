# Configuring ARLAS Tagger running environment

## ARLAS Tagger configuration file

ARLAS tagger is configured with a yaml configuration file.

External module configurations are available online:

| Module  | Link                                            |
|---------|-------------------------------------------------|
| Swagger | https://github.com/federecio/dropwizard-swagger |

## Configure ARLAS Tagger as a docker container

#### With environment variables

ARLAS Tagger can run as a docker container. A rich set of properties of the configuration file can be overriden by passing environment variables to the container:

```shell
docker run -ti -d \
   --name arlas-tagger \
   -e "ARLAS_ELASTIC_CLUSTER=my-own-cluster" \
   -e "ARLAS_ELASTIC_NODES=my-host:my-port" \
   -e "KAFKA_BROKERS=my-own-kafka-brokers" \
   gisaia/arlas-tagger:latest
```

All supported environment variables are listed below.


### With file/URL based configuration

Instead of overriding some properties of the configuration file, it is possible to start the ARLAS Tagger container with a given configuration file.

#### File

The ARLAS Tagger container can start with a mounted configuration file thanks to docker volume mapping. For instance, if the current directory of the host contains a `configuration.yaml` file, the container can be started as follow:

```shell
docker run -ti -d \
   --name arlas-tagger \
   -v `pwd`/configuration.yaml:/opt/app/configuration.yaml \
   gisaia/arlas-tagger:latest
```

#### URL

The ARLAS Tagger container can start with a configuration file that is downloaded before starting up. The configuration file must be available through an URL accessible from within the container. The URL is specified with an environment variable:

| Environment variable           | Description                                                                                  |
|--------------------------------|----------------------------------------------------------------------------------------------|
| ARLAS_TAGGER_CONFIGURATION_URL | URL of the ARLAS Tagger configuration file to be downloaded by the container before starting |

For instance, if the current directory of the host contains a `configuration.yaml` file, the container can be started as follow:

```shell
docker run -ti -d \
   --name arlas-tagger \
   -e ARLAS_TAGGER_CONFIGURATION_URL="http://somemachine/conf.yaml" \
   gisaia/arlas-tagger:latest
```
## ARLAS Tagger configuration properties

### ARLAS Tagger

| Environment variable   | ARLAS Tagger configuration variable | Default | Description                              |
|------------------------|-------------------------------------|---------|------------------------------------------|
| TAGGING_STATUS_TIMEOUT | tagging_status_timeout              | 3600000 | Delay before tagging status is discarded |

### Elasticsearch

| Environment variable       | ARLAS Tagger configuration variable    | Default        | Description                                                                           |
|----------------------------|----------------------------------------|----------------|---------------------------------------------------------------------------------------|
| ARLAS_ELASTIC_NODES        | elastic_configuration.elastic_nodes    | localhost:9200 | comma separated list of elasticsearch nodes as host:port values                       |
| ARLAS_ELASTIC_SNIFFING     | elastic_configuration.elastic_sniffing | false          | allow elasticsearch to dynamically add new hosts and remove old ones (*)              |
| ARLAS_ELASTIC_CLUSTER (**) | elastic_configuration.elastic_cluster  | elasticsearch  | clustername of the elasticsearch cluster that is used for storing ARLAS configuration |
| ARLAS_ELASTIC_ENABLE_SSL   | elastic-enable-ssl                     | false          | use SSL to connect to elasticsearch                                                   |
| ARLAS_ELASTIC_CREDENTIALS  | elastic-credentials                    | user:password  | credentials to connect to elasticsearch                                               |
| ARLAS_ELASTIC_SKIP_MASTER  | elastic-skip-master                    | true           | Skip dedicated master in Rest client                                                  |

!!! note 
    (*) Note that the IP addresses the sniffer connects to are the ones declared as the publish address in those node’s Elasticsearch config.
    (**) Deprecated

### AUTH

| Environment variable              | ARLAS Server configuration variable | Default                                     | Description                                                                          |
|-----------------------------------|-------------------------------------|---------------------------------------------|--------------------------------------------------------------------------------------|
| ARLAS_AUTH_POLICY_CLASS           | arlas_auth_policy_class             | io.arlas.commons.rest.auth.NoPolicyEnforcer | Specify a PolicyEnforcer class to load in order to activate Authentication if needed |

### Kafka

| Environment variable                 | ARLAS Tagger configuration variable                      | Default                     | Description                                                                                                                                                                              |
|--------------------------------------|----------------------------------------------------------|-----------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| KAFKA_COMMIT_MAX_RETRIES             | kafka_configuration.kafka_commit_max_retries             | 3                           | Number of commit retries in case of failure before exiting                                                                                                                               |
| KAFKA_CONSUMER_POLL_TIMEOUT          | kafka_configuration.kafka_consumer_poll_timeout          | 100                         | Kafka consumer poll timeout                                                                                                                                                              |
| KAFKA_BATCH_SIZE_TAGREF              | kafka_configuration.kafka_batch_size_tagref              | 1                           | Kafka consumer batch size for tagref_log consumer                                                                                                                                        |
| KAFKA_BATCH_SIZE_TAGEXEC             | kafka_configuration.kafka_batch_size_tagexec             | 10                          | Kafka consumer batch size for execute_tags consumer                                                                                                                                      |
| KAFKA_NUMBER_TAGEXEC                 | kafka_configuration.kafka_number_tagexec                 | 1                           | Number of execute_tags consumers                                                                                                                                                         |
| KAFKA_BROKERS                        | kafka_configuration.kafka_bootstrap_servers              | kafka:9092                  | Kafka brokers                                                                                                                                                                            |
| KAFKA_CONSUMER_GROUP_ID_TAGREF_LOG   | kafka_configuration.kafka_consumer_group_id_tagref_log   | tagref_log_consumer_group   | Kafka consumer group for `tagref_log` topic execution                                                                                                                                    |
| KAFKA_CONSUMER_GROUP_ID_EXPLORE_TAGS | kafka_configuration.kafka_consumer_group_id_explore_tags | explore_tags_consumer_group | Kafka consumer group for `tagref_log` topic exploration (replay, list)                                                                                                                   |
| KAFKA_CONSUMER_GROUP_ID_EXECUTE_TAGS | kafka_configuration.kafka_consumer_group_id_execute_tags | execute_tags_consumer_group | Kafka consumer group for `execute_tags` topic                                                                                                                                            |
| KAFKA_TOPIC_TAGREF_LOG               | kafka_configuration.kafka_topic_tagref_log               | tagref_log                  | Kafka topic for tag requests queue (tag log, retained)                                                                                                                                   |
| KAFKA_TOPIC_EXECUTE_TAGS             | kafka_configuration.kafka_topic_execute_tags             | execute_tags                | Kafka topic for actual tag requests (actually executed, not retained)                                                                                                                    |
| KAFKA_EXTRA_PROPS                    | kafka_configuration.kafka_extra_properties               | -                           | Comma separated properties for configuring the kafka client. For instance `kafka_configuration.kafka_extra_properties: ssl.endpoint.identification.algorithm=https,sasl.mechanism=PLAIN` |

### ARLAS Collection 
| Environment variable        | ARLAS Tagger configuration variable              | Default | Description                                                  |
|-----------------------------|--------------------------------------------------|---------|--------------------------------------------------------------|
| ARLAS_COLLECTION_INDEX      | arlas_collections_configuration.arlas_index      | .arlas  | name of the index that is used for storing ARLAS collections |
| ARLAS_COLLECTION_CACHE_SIZE | arlas_collections_configuration.arlas_cache_size | 1000    | Size of the cache used for managing the collections          |

### Server

| Environment variable             | ARLAS Tagger configuration variable                    | Default                                                                                       |
|----------------------------------|--------------------------------------------------------|-----------------------------------------------------------------------------------------------|
| ARLAS_TAGGER_CORS_ENABLED        | arlas_cors_enabled                                     | whether the Cross-Origin Resource Sharing (CORS) mechanism is enabled or not. Default : true. |
| ARLAS_TAGGER_ACCESS_LOG_FILE     | server.requestLog.appenders.currentLogFilename         | arlas-access.log                                                                              |
| ACCESS_TAGGER_LOG_FILE_ARCHIVE   | server.requestLog.appenders.archivedLogFilenamePattern | arlas-access-%d.log.gz                                                                        |
| ARLAS_TAGGER_PREFIX              | server.applicationContextPath                          | /arlas/                                                                                       |
| ARLAS_TAGGER_ADMIN_PATH          | server.adminContextPath                                | /admin                                                                                        |
| ARLAS_TAGGER_PORT                | server.connector.port                                  | 9999                                                                                          |
| ARLAS_TAGGER_MAX_THREADS         | server.maxThreads                                      | 1024                                                                                          |
| ARLAS_TAGGER_MIN_THREADS         | server.minThreads                                      | 8                                                                                             |
| ARLAS_TAGGER_MAX_QUEUED_REQUESTS | server.maxQueuedRequests                               | 1024                                                                                          |

### Logging

| Environment variable                    | ARLAS Tagger configuration variable                      | Default      |
|-----------------------------------------|----------------------------------------------------------|--------------|
| ARLAS_LOGGING_LEVEL                     | logging.level                                            | INFO         |
| ARLAS_LOGGING_CONSOLE_LEVEL             | logging.appenders[type: console].threshold               | INFO         |
| ARLAS_LOGGING_FILE                      | logging.appenders[type: file].currentLogFilename         | arlas.log    |
| ARLAS_LOGGING_FILE_LEVEL                | logging.appenders[type: file].threshold                  | INFO         |
| ARLAS_LOGGING_FILE_ARCHIVE              | logging.appenders[type: file].archive                    | true         |
| ARLAS_LOGGING_FILE_ARCHIVE_FILE_PATTERN | logging.appenders[type: file].archivedLogFilenamePattern | arlas-%d.log |
| ARLAS_LOGGING_FILE_ARCHIVE_FILE_COUNT   | logging.appenders[type: file].archivedFileCount          | 5            |

### CACHE

| Environment variable                  | ARLAS Server configuration variable | Default                               | Description                            |
|---------------------------------------|-------------------------------------|---------------------------------------|----------------------------------------|
| ARLAS_PERSISTENCE_CACHE_FACTORY_CLASS | arlas_cache_factory_class           | io.arlas.commons.cache.NoCacheFactory | Factory class to get the cache manager |
| ARLAS_CACHE_TIMEOUT                   | arlas-cache-timeout                 | 60                                    | TTL in seconds of items in the cache   |

### JAVA

| Environment variable | Description            |
|----------------------|------------------------|
| ARLAS_XMX            | Java Maximum Heap Size |