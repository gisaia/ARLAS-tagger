# Configuring ARLAS Tagger running environment

## ARLAS Tagger configuration file

ARLAS tagger is configured with a yaml configuration file.

External module configurations are available online:

| Module | Link |
| --- | --- |
| Swagger | https://github.com/federecio/dropwizard-swagger |

## Configure ARLAS Tagger as a docker container

#### With environment variables

ARLAS Tagger can run as a docker container. A rich set of properties of the configuration file can be overriden by passing environment variables to the container:

```shell
docker run -ti -d \
   --name arlas-tagger \
   -e "ARLAS_ELASTIC_CLUSTER=my-own-cluster" \
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

| Environment variable | Description |
| --- | --- |
| ARLAS_TAGGER_CONFIGURATION_URL | URL of the ARLAS Tagger configuration file to be downloaded by the container before starting |

For instance, if the current directory of the host contains a `configuration.yaml` file, the container can be started as follow:

```shell
docker run -ti -d \
   --name arlas-tagger \
   -e ARLAS_TAGGER_CONFIGURATION_URL="http://somemachine/conf.yaml" \
   gisaia/arlas-tagger:latest
```
## ARLAS Tagger configuration properties

### Elasticsearch

| Environment variable   | ARLAS Tagger configuration variable | Default | Description |
| --- | --- | --- | ---  |
| ARLAS_ELASTIC_NODES    | elastic-nodes    | localhost:9300 | comma separated list of elasticsearch nodes as host:port values |
| ARLAS_ELASTIC_SNIFFING | elastic-sniffing | false          | allow elasticsearch to dynamically add new hosts and remove old ones (*)|
| ARLAS_ELASTIC_CLUSTER  | elastic-cluster  | elasticsearch  | clustername of the elasticsearch cluster that is used for storing ARLAS configuration |

!!! note 
    (*) Note that the IP addresses the sniffer connects to are the ones declared as the publish address in those node’s Elasticsearch config.

### Kafka

| Environment variable | ARLAS Tagger configuration variable | Default | Description |
| --- | --- | --- | --- |
| TAGGING_STATUS_TIMEOUT | arlas-tagger.status-timeout | 3600000 | Delay before tagging status is discarded |
| KAFKA_CONSUMER_POLL_TIMEOUT | arlas-tagger.kafka-consumer-poll-timeout | 100 | Kafka consumer poll timeout |
| KAFKA_BATCH_SIZE | arlas-tagger.kafka-batch-size | 10 | Kafka consumer batch size |
| KAFKA_BROKERS | arlas-tagger.kafka-bootstrap-servers | kafka:9092 | Kafka brokers|
| KAFKA_CONSUMER_GROUP_ID_TAGREF_LOG | arlas-tagger.kafka-consumer-group-id-tagref-log | tagref_log_consumer_group | Kafka consumer group for `tagref_log` topic |
| KAFKA_CONSUMER_GROUP_ID_EXECUTE_TAGS | arlas-tagger.kafka-consumer-group-id-execute-tags | execute_tags_consumer_group | Kafka consumer group for `execute_tags` topic |
| KAFKA_TOPIC_TAGREF_LOG | arlas-tagger.kafka-topic-tagref-log | tagref_log | Kafka topic for tag requests queue (tag log, retained) |
| KAFKA_TOPIC_EXECUTE_TAGS | arlas-tagger.kafka-topic-execute-tags | execute_tags | Kafka topic for actual tag requests (actually executed, not retained) |

### ARLAS Collection 
| Environment variable | ARLAS Tagger configuration variable | Default | Description |
| --- | --- | --- | --- |
| ARLAS_COLLECTION_INDEX    | arlas-index      | .arlas         | name of the index that is used for storing ARLAS collections |
| ARLAS_CACHE_SIZE       | arlas-cache-size                  | 1000 | Size of the cache used for managing the collections  |
| ARLAS_CACHE_TIMEOUT    | arlas-cache-timeout               | 60 | Number of seconds for the cache used for managing the collections |

### Server

| Environment variable | ARLAS Tagger configuration variable | Default |
| --- | --- | --- |
| ARLAS_TAGGER_CORS_ENABLED | arlas-cors-enabled            | whether the Cross-Origin Resource Sharing (CORS) mechanism is enabled or not. Default : true. |
| ARLAS_TAGGER_ACCESS_LOG_FILE | server.requestLog.appenders.currentLogFilename | arlas-access.log |
| ACCESS_TAGGER_LOG_FILE_ARCHIVE | server.requestLog.appenders.archivedLogFilenamePattern | arlas-access-%d.log.gz |
| ARLAS_TAGGER_PREFIX | server.applicationContextPath | /arlas/ |
| ARLAS_TAGGER_ADMIN_PATH | server.adminContextPath | /admin |
| ARLAS_TAGGER_PORT | server.connector.port | 9999 |
| ARLAS_TAGGER_MAX_THREADS | server.maxThreads | 1024 |
| ARLAS_TAGGER_MIN_THREADS | server.minThreads | 8 |
| ARLAS_TAGGER_MAX_QUEUED_REQUESTS | server.maxQueuedRequests | 1024 |

### JAVA

| Environment variable | Description |
| --- | --- |
| ARLAS_XMX | Java Maximum Heap Size |