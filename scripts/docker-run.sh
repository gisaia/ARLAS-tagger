#!/bin/bash
set -e

SCRIPT_DIRECTORY="$(cd "$(dirname "${BASH_SOURCE[0]}" )" >/dev/null && pwd)"
PROJECT_ROOT_DIRECTORY="$(dirname "$SCRIPT_DIRECTORY")"
BUILD_OPTS="--no-build"
DOCKER_COMPOSE_TAGGER="${PROJECT_ROOT_DIRECTORY}/docker/docker-files/docker-compose.yml"
DOCKER_COMPOSE_ES="${PROJECT_ROOT_DIRECTORY}/docker/docker-files/docker-compose-elasticsearch.yml"
DOCKER_COMPOSE_ARLAS_SERVER="${PROJECT_ROOT_DIRECTORY}/docker/docker-files/docker-compose-arlas-server.yml"

for i in "$@"
do
case $i in
    -es=*|--elasticsearch=*)
    export ELASTIC_DATADIR="${i#*=}"
    DOCKER_COMPOSE_ARGS="${DOCKER_COMPOSE_ARGS} -f ${DOCKER_COMPOSE_ES}"
    shift # past argument=value
    ;;
    -k=*|--kafka=*)
    export KAFKA_DATADIR="${i#*=}"
    shift # past argument with no value
    ;;
    --server=*)
    DOCKER_COMPOSE_ARGS="${DOCKER_COMPOSE_ARGS} -f ${DOCKER_COMPOSE_ARLAS_SERVER}"
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
	maven:3.5.0-jdk-8 \
	mvn clean install -B -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn
echo "arlas-tagger:${ARLAS_TAGGER_VERSION}"

echo "===> start arlas-tagger stack"
docker-compose -f ${DOCKER_COMPOSE_TAGGER} ${DOCKER_COMPOSE_ARGS} --project-name arlas up -d ${BUILD_OPTS}

#docker logs -f arlas-tagger &

echo "===> wait for arlas-tagger up and running"
docker run --net arlas_default --rm busybox sh -c 'i=1; until nc -w 2 arlas-tagger 9998; do if [ $i -lt 30 ]; then sleep 1; else break; fi; i=$(($i + 1)); done'
