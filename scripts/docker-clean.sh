#!/bin/bash
set -e

SCRIPT_DIRECTORY="$(cd "$(dirname "${BASH_SOURCE[0]}" )" >/dev/null && pwd)"
PROJECT_ROOT_DIRECTORY="$(dirname "$SCRIPT_DIRECTORY")"
DOCKER_COMPOSE_TAGGER="${PROJECT_ROOT_DIRECTORY}/docker/docker-files/docker-compose.yml"
DOCKER_COMPOSE_ES="${PROJECT_ROOT_DIRECTORY}/docker/docker-files/docker-compose-elasticsearch.yml"
DOCKER_COMPOSE_KAFKA="${PROJECT_ROOT_DIRECTORY}/docker/docker-files/docker-compose-kafka.yml"
DOCKER_COMPOSE_ARLAS_SERVER="${PROJECT_ROOT_DIRECTORY}/docker/docker-files/docker-compose-arlas-server.yml"

function clean_exit {
    ARG=$?
    exit $ARG
}
trap clean_exit EXIT

echo "===> stop arlas-tagger stack"
docker compose -f ${DOCKER_COMPOSE_TAGGER} -f ${DOCKER_COMPOSE_ARLAS_SERVER} -f ${DOCKER_COMPOSE_ES} -f ${DOCKER_COMPOSE_KAFKA} --project-name arlas down -v