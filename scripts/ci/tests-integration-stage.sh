#!/bin/bash
set -o errexit -o pipefail

function clean_docker {
  ./scripts/docker-clean.sh
  echo "===> clean maven repository"
	docker run --rm \
		-w /opt/maven \
		-v $PWD:/opt/maven \
		-v $HOME/.m2:/root/.m2 \
		maven:3.8.5-openjdk-17 \
		mvn clean
}

function clean_exit {
  ARG=$?
  # Allow errors on cleanup
  set +e

  if [[ "$ARG" != 0 ]]; then
      # In case of error, print containers logs (if any)
      #docker logs elasticsearch
      docker logs arlas-server
      docker logs arlas-tagger
  fi
	echo "===> Exit stage ${STAGE} = ${ARG}"
  clean_docker
  exit $ARG
}
trap clean_exit EXIT

usage(){
	echo "Usage: ./tests-integration-stage.sh --stage=TAG|AUTH"
	exit 1
}

for i in "$@"
do
case $i in
    --stage=*)
    STAGE="${i#*=}"
    shift # past argument=value
    ;;
    *)
            # unknown option
    ;;
esac
done

# GO TO PROJECT PATH
SCRIPT_PATH=`cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd`
cd ${SCRIPT_PATH}/../..

# CHECK ALV2 DISCLAIMER
if [ $(find ./*/src -name "*.java" -exec grep -L Licensed {} \; | wc -l) -gt 0 ]; then
    echo "[ERROR] ALv2 disclaimer is missing in the following files :"
    find ./*/src -name "*.java" -exec grep -L Licensed {} \;
    exit -1
fi

if [ -z ${STAGE+x} ]; then usage; else echo "Tests stage : ${STAGE}"; fi

function start_stack() {
    # START ARLAS STACK
    ./scripts/docker-clean.sh
    ./scripts/docker-run.sh -es=/tmp -k=/tmp --server=/tmp --build
}


function test_tagger() {
    export ARLAS_PREFIX="/arlastest"
    export ARLAS_APP_PATH="/pathtest"
    export ARLAS_AUTH_ENABLED=false
    export ARLAS_SERVICE_EXPLORE_ENABLE=true
    export ARLAS_TAGGER_PREFIX="/arlastaggertest"
    export ARLAS_TAGGER_APP_PATH="/pathtaggertest"
    export ARLAS_SERVER_NODE="arlas-server:9999"
    start_stack
    docker run --rm \
        -w /opt/maven \
        -v $PWD:/opt/maven \
        -v $HOME/.m2:/root/.m2 \
        -e ARLAS_HOST="arlas-server" \
        -e ARLAS_PORT="9999" \
        -e ARLAS_PREFIX=${ARLAS_PREFIX} \
        -e ARLAS_APP_PATH=${ARLAS_APP_PATH} \
        -e ARLAS_TAGGER_HOST="arlas-tagger" \
        -e ARLAS_TAGGER_PORT="9998" \
        -e ARLAS_TAGGER_PREFIX=${ARLAS_TAGGER_PREFIX} \
        -e ARLAS_TAGGER_APP_PATH=${ARLAS_TAGGER_APP_PATH} \
        -e ARLAS_ELASTIC_NODES="elasticsearch:9200" \
        -e ARLAS_SERVER_NODE=${ARLAS_SERVER_NODE} \
        -e ALIASED_COLLECTION=${ALIASED_COLLECTION} \
        --net arlas_default \
        maven:3.8.5-openjdk-17 \
        mvn -Dit.test=TagIT verify -DskipTests=false -DfailIfNoTests=false -B
}

function test_tagger_with_auth() {
  echo "Auth tests skipped until a solution is found to test with UMS"
#    export ARLAS_PREFIX="/arlastest"
#    export ARLAS_APP_PATH="/pathtest"
#    export ARLAS_AUTH_ENABLED=true
#    export ARLAS_SERVICE_EXPLORE_ENABLE=true
#    export ARLAS_TAGGER_PREFIX="/arlastaggertest"
#    export ARLAS_TAGGER_APP_PATH="/pathtaggertest"
#    export ARLAS_SERVER_NODE="arlas-server:9999"
#    export ARLAS_AUTH_LOCAL_CERT_FILE="/opt/app/arlas-test.pem"
#    start_stack
#    docker run --rm \
#        -w /opt/maven \
#        -v $PWD:/opt/maven \
#        -v $HOME/.m2:/root/.m2 \
#        -e ARLAS_HOST="arlas-server" \
#        -e ARLAS_PORT="9999" \
#        -e ARLAS_PREFIX=${ARLAS_PREFIX} \
#        -e ARLAS_APP_PATH=${ARLAS_APP_PATH} \
#        -e ARLAS_TAGGER_HOST="arlas-tagger" \
#        -e ARLAS_TAGGER_PORT="9998" \
#        -e ARLAS_TAGGER_PREFIX=${ARLAS_TAGGER_PREFIX} \
#        -e ARLAS_TAGGER_APP_PATH=${ARLAS_TAGGER_APP_PATH} \
#        -e ARLAS_ELASTIC_NODES="elasticsearch:9200" \
#        -e ARLAS_SERVER_NODE=${ARLAS_SERVER_NODE} \
#        -e ALIASED_COLLECTION=${ALIASED_COLLECTION} \
#        --net arlas_default \
#        maven:3.8.5-openjdk-17 \
#        mvn -Dit.test=TagAuthIT verify -DskipTests=false -DfailIfNoTests=false -B
}

function test_doc() {
    ./mkDocs.sh
}

if [ ! -z ${DOCKER_USERNAME+x} ] && [ ! -z ${DOCKER_PASSWORD+x} ]
then
  echo "$DOCKER_PASSWORD" | docker login -u "$DOCKER_USERNAME" --password-stdin
fi

if [ "$STAGE" == "TAG" ]; then test_tagger; fi
if [ "$STAGE" == "DOC" ]; then test_doc; fi
if [ "$STAGE" == "AUTH" ]; then test_tagger_with_auth; fi

