#!/bin/bash
set -o errexit -o pipefail

export ELASTIC_VERSION="7.15.2"
export ARLAS_VERSION="21.0.0"

function clean_exit {
  ARG=$?
	echo "===> Exit status = ${ARG}"
  exit $ARG
}
trap clean_exit EXIT

usage(){
	echo "Usage: ./tests-integration.sh [--es=X.Y.Z]"
	echo " --es=X.Y.Z   elasticsearch version to test"
	exit 1
}

for i in "$@"
do
case $i in
    --es=*)
    ELASTIC_VERSION="${i#*=}"
    shift # past argument=value
    ;;
    *)
            # unknown option
    ;;
esac
done

if [ -z ${ELASTIC_VERSION+x} ]; then usage; else echo "ARLAS-tagger tested with ARLAS-server : ${ARLAS_VERSION} and ES version : ${ELASTIC_VERSION}"; fi
export ELASTIC_VERSION=${ELASTIC_VERSION}
export ARLAS_VERSION=${ARLAS_VERSION}

# GO TO PROJECT PATH
SCRIPT_PATH=`cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd`
cd ${SCRIPT_PATH}/../..

# TESTS SUITE
./scripts/ci/tests-integration-stage.sh --stage=TAG
./scripts/ci/tests-integration-stage.sh --stage=AUTH
