#!/bin/bash
set -e

SCRIPT_DIRECTORY="$(cd "$(dirname "${BASH_SOURCE[0]}" )" >/dev/null && pwd)"
PROJECT_ROOT_DIRECTORY="$(dirname "$SCRIPT_DIRECTORY")"
BUILD_OPTS="--no-build"
DOCKER_COMPOSE_TAGGER="${PROJECT_ROOT_DIRECTORY}/docker/docker-files/docker-compose.yml"
DOCKER_COMPOSE_ES="${PROJECT_ROOT_DIRECTORY}/docker/docker-files/docker-compose-elasticsearch.yml"
DOCKER_COMPOSE_KAFKA="${PROJECT_ROOT_DIRECTORY}/docker/docker-files/docker-compose-kafka.yml"
DOCKER_COMPOSE_ARLAS_SERVER="${PROJECT_ROOT_DIRECTORY}/docker/docker-files/docker-compose-arlas-server.yml"

for i in "$@"
do
case $i in
    -es=*|--elasticsearch=*)
    export ELASTIC_DATADIR="${i#*=}"
    shift # past argument=value
    ;;
    -k=*|--kafka=*)
    export KAFKA_DATADIR="${i#*=}"
    shift # past argument with no value
    ;;
    --server=*)
    export SERVER_ENABLED="true"
    shift # past argument with no value
    ;;
    --build)
    BUILD_OPTS="--build"
    shift # past argument with no value
    ;;
    *)
            # unknown option
    ;;
esac
done

function clean_exit {
    ARG=$?
    exit $ARG
}
trap clean_exit EXIT

export ARLAS_TAGGER_VERSION=`xmlstarlet sel -t -v /_:project/_:version pom.xml`

# GO TO PROJECT PATH
SCRIPT_PATH=`cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd`
cd ${SCRIPT_PATH}/..

# PACKAGE
echo "===> compile arlas-tagger"
docker run --rm \
    -w /opt/maven \
	-v $PWD:/opt/maven \
	-v $HOME/.m2:/root/.m2 \
	maven:3.8.5-openjdk-17 \
	mvn clean install -B -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn
echo "arlas-tagger:${ARLAS_TAGGER_VERSION}"

echo "===> start arlas-tagger stack"
if [ -z "${ELASTIC_DATADIR}" ]; then
  echo "An external ES is used"
else
  echo "Starting ES"
  docker-compose -f ${DOCKER_COMPOSE_ES} --project-name arlas up -d
  echo "Waiting for ES readiness"
  docker run --net arlas_default --rm busybox sh -c 'i=1; until nc -w 2 elasticsearch 9200; do if [ $i -lt 30 ]; then sleep 1; else break; fi; i=$(($i + 1)); done'
  echo "ES is ready"
fi

if [ -z "${KAFKA_DATADIR}" ]; then
  echo "An external Kafka is used"
else
  echo "Starting Kafka"
  docker-compose -f ${DOCKER_COMPOSE_KAFKA} --project-name arlas up -d
  echo "Waiting for Kafka readiness"
  docker run --net arlas_default --rm busybox sh -c 'i=1; until nc -w 2 kafka 9092; do if [ $i -lt 30 ]; then sleep 1; else break; fi; i=$(($i + 1)); done'
  echo "Kafka is ready"
fi

if [ -z "${SERVER_ENABLED}" ]; then
  echo "An external ARLAS Server is used"
else
  echo "Starting ARLAS Server"
  docker-compose -f ${DOCKER_COMPOSE_ARLAS_SERVER} --project-name arlas up -d
  echo "Waiting for ARLAS Server readiness"
  docker run --net arlas_default --rm busybox sh -c 'i=1; until nc -w 2 arlas-server 9999; do if [ $i -lt 30 ]; then sleep 1; else break; fi; i=$(($i + 1)); done'
  echo "ARLAS Server is ready"
fi

docker-compose -f ${DOCKER_COMPOSE_TAGGER} --project-name arlas up -d ${BUILD_OPTS}

#docker logs -f arlas-tagger &

echo "===> wait for arlas-tagger up and running"
docker run --net arlas_default --rm busybox sh -c 'i=1; until nc -w 2 arlas-tagger 9998; do if [ $i -lt 30 ]; then sleep 1; else break; fi; i=$(($i + 1)); done'
