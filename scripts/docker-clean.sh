#!/bin/bash
set -e

function clean_exit {
    ARG=$?
    exit $ARG
}
trap clean_exit EXIT

echo "===> stop arlas-tagger stack"
docker-compose -f docker-compose-tagger.yml -f docker-compose-arlas-server.yml -f docker-compose-elasticsearch.yml --project-name arlas down -v