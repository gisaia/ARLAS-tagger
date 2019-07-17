#!/bin/bash
set -e

function clean_docker {
    ./scripts/docker-clean.sh
    echo "===> clean maven repository"
	docker run --rm \
		-w /opt/maven \
		-v $PWD:/opt/maven \
		-v $HOME/.m2:/root/.m2 \
		maven:3.5.0-jdk-8 \
		mvn clean
}

function clean_exit {
    ARG=$?
	echo "===> Exit stage ${STAGE} = ${ARG}"
    clean_docker
    exit $ARG
}
trap clean_exit EXIT

usage(){
	echo "Usage: ./test-integration-stage.sh --stage=TAG"
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
cd ${SCRIPT_PATH}/..

# CHECK ALV2 DISCLAIMER
if [ $(find ./*/src -name "*.java" -exec grep -L Licensed {} \; | wc -l) -gt 0 ]; then
    echo "ALv2 disclaimer is missing in the following files :"
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
    export ARLAS_SERVICE_EXPLORE_ENABLE=true
    export ARLAS_TAGGER_PREFIX="/arlastaggertest"
    export ARLAS_TAGGER_APP_PATH="/pathtaggertest"
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
        -e ARLAS_ELASTIC_NODES="elasticsearch:9300" \
        -e ALIASED_COLLECTION=${ALIASED_COLLECTION} \
        --net arlas_default \
        maven:3.5.0-jdk-8 \
        mvn -Dit.test=TagIT verify -DskipTests=false -DfailIfNoTests=false
}

if [ "$STAGE" == "TAG" ]; then test_tagger; fi
