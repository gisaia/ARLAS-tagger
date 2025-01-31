#!/bin/bash
set -o errexit -o pipefail

export RELEASE_COMMAND_LINE="$0 $*"

SCRIPT_DIRECTORY="$(cd "$(dirname "${BASH_SOURCE[0]}")" >/dev/null && pwd)"
PROJECT_ROOT_DIRECTORY="$SCRIPT_DIRECTORY"

npmlogin=`npm whoami`
if  [ -z "$npmlogin"  ] ; then echo "your are not logged on npm"; exit -1; else  echo "logged as "$npmlogin ; fi

#########################################
#### Variables intialisation ############
#########################################
TEST="YES"
RELEASE="NO"
SKIP_API="NO"
BASEDIR=$PWD
DOCKER_COMPOSE_TAGGER="${PROJECT_ROOT_DIRECTORY}/docker/docker-files/docker-compose.yml"
DOCKER_COMPOSE_ES="${PROJECT_ROOT_DIRECTORY}/docker/docker-files/docker-compose-elasticsearch.yml"
DOCKER_COMPOSE_KAFKA="${PROJECT_ROOT_DIRECTORY}/docker/docker-files/docker-compose-kafka.yml"

#########################################
#### Cleaning functions #################
#########################################
function clean_docker {
    echo "===> Stop arlas-tagger stack"
    docker compose -f ${DOCKER_COMPOSE_TAGGER} -f ${DOCKER_COMPOSE_ES} -f ${DOCKER_COMPOSE_KAFKA} --project-name arlas down -v
}

function clean_exit {
  ARG=$?
	echo "=> Exit status = $ARG"
	rm -rf pom.xml.versionsBackup
	rm -rf target/tmp || echo "target/tmp already removed"
	clean_docker
	if [ "$RELEASE" == "YES" ]; then
        git checkout -- .
        mvn clean
    else
        echo "=> Skip discard changes";
        git checkout -- pom.xml
        git checkout -- arlas-tagger/pom.xml
        git checkout -- arlas-tagger-core/pom.xml
        git checkout -- arlas-tagger-rest/pom.xml
        git checkout -- arlas-tagger-tests/pom.xml
        sed -i.bak 's/\"'${FULL_API_VERSION}'\"/\"API_VERSION\"/' arlas-tagger-rest/src/main/java/io/arlas/tagger/rest/tag/TagRESTService.java
    fi
    exit $ARG
}
trap clean_exit EXIT

#########################################
#### Available arguments ################
#########################################
usage(){
	echo "Usage: ./release.sh -api-major=X -api-minor=Y -api-patch=U -rel=Z -dev=Z+1 -es=Y [--no-tests] [--skip-api]"
	echo " -api-major|--api-version       release arlas-tagger API major version"
	echo " -api-minor|--api-minor-version release arlas-tagger API minor version"
	echo " -api-patch|--api-patch-version release arlas-tagger API patch version"
	echo " -dev|--arlas-dev               development arlas-tagger version (-SNAPSHOT qualifier will be automatically added)"
	echo " --no-tests                     do not run integration tests"
	echo " --release                      publish artifacts and git push local branches"
	echo " --skip-api                     do not generate clients APIs"
	exit 1
}


#########################################
#### Chat message ################
#########################################
send_chat_message(){
    MESSAGE=$1
    if [ -z "$GOOGLE_CHAT_RELEASE_CHANEL" ] ; then
        echo "Environement variable GOOGLE_CHAT_RELEASE_CHANEL is not definied ... skipping message publishing"
    else
        DATA='{"text":"'${MESSAGE}'"}'
        echo $DATA
        curl -X POST --header "Content-Type:application/json" $GOOGLE_CHAT_RELEASE_CHANEL -d "${DATA}"
    fi
}

#########################################
#### Parsing arguments ##################
#########################################
for i in "$@"
do
case $i in
    -dev=*|--arlas-dev=*)
    ARLAS_DEV="${i#*=}"
    shift # past argument=value
    ;;
    -api-major=*|--api-major-version=*)
    API_MAJOR_VERSION="${i#*=}"
    shift # past argument=value
    ;;
    -api-minor=*|--api-minor-version=*)
    API_MINOR_VERSION="${i#*=}"
    shift # past argument=value
    ;;
    -api-patch=*|--api-patch-version=*)
    API_PATCH_VERSION="${i#*=}"
    shift # past argument=value
    ;;
    --no-tests)
    TESTS="NO"
    shift # past argument with no value
    ;;
    --release)
    RELEASE="YES"
    shift # past argument with no value
    ;;
    --skip-api)
    SKIP_API="YES"
    shift # past argument with no value
    ;;
    *)
            # unknown option
    ;;
esac
done

#########################################
#### Recap of chosen arguments ##########
#########################################

if [ -z ${API_MAJOR_VERSION+x} ]; then usage;  else    echo "API MAJOR version           : ${API_MAJOR_VERSION}"; fi
if [ -z ${API_MINOR_VERSION+x} ]; then usage;  else    echo "API MINOR version           : ${API_MINOR_VERSION}"; fi
if [ -z ${API_PATCH_VERSION+x} ]; then usage;  else    echo "API PATCH version           : ${API_PATCH_VERSION}"; fi
if [ -z ${ARLAS_DEV+x} ]; then usage;          else    echo "Next development version    : ${ARLAS_DEV}"; fi
                                                       echo "Running tests               : ${TESTS}"
                                                       echo "Release                     : ${RELEASE}"

#########################################
#### Check if you're logged on to repos ###########
#########################################

if [ "$RELEASE" == "YES" -a "$SKIP_API" == "NO" ]; then
    export npmlogin=`npm whoami`
    if  [ -z "$npmlogin"  ] ; then echo "Your are not logged on to npm"; exit -1; else  echo "logged as "$npmlogin ; fi
    if  [ -z "$GITHUB_CHANGELOG_TOKEN"  ] ; then echo "Please set GITHUB_CHANGELOG_TOKEN environment variable"; exit -1; fi
fi


#########################################
#### Setting versions ###################
#########################################
export ARLAS_TAGGER_VERSION="${API_MAJOR_VERSION}.${API_MINOR_VERSION}.${API_PATCH_VERSION}"
ARLAS_DEV_VERSION="${API_MAJOR_VERSION}.${API_MINOR_VERSION}.${ARLAS_DEV}"
FULL_API_VERSION=${API_MAJOR_VERSION}"."${API_MINOR_VERSION}"."${API_PATCH_VERSION}
API_DEV_VERSION=${API_MAJOR_VERSION}"."${API_MINOR_VERSION}"."${ARLAS_DEV}

echo "Release : ${ARLAS_TAGGER_VERSION}"
echo "API     : ${FULL_API_VERSION}"
echo "Dev     : ${ARLAS_DEV_VERSION}"


#########################################
#### Ongoing release process ############
#########################################

echo "=> Get develop branch"
if [ "$RELEASE" == "YES" ]; then
    git checkout develop
    git pull origin develop
else echo "=> Skip develop checkout"; fi

echo "=> Update project version"
mvn clean
mvn versions:set -DnewVersion=${ARLAS_TAGGER_VERSION}
sed -i.bak 's/\"API_VERSION\"/\"'${FULL_API_VERSION}'\"/' arlas-tagger-rest/src/main/java/io/arlas/tagger/rest/tag/TagRESTService.java

if [ "$RELEASE" == "YES" ]; then
    export DOCKERFILE="${PROJECT_ROOT_DIRECTORY}/docker/docker-files/Dockerfile"
else
    echo "=> Build arlas-tagger"
    docker run \
        -e GROUP_ID="$(id -g)" \
        -e USER_ID="$(id -u)" \
        --mount dst=/mnt/.m2,src="$HOME/.m2/",type=bind \
        --mount dst=/opt/maven,src="$PWD",type=bind \
        --rm \
        gisaia/maven-3.5-jdk8-alpine \
            clean install
fi

#########################################
#### Generate swagger definiton of the API #######
#########################################

echo "=> Start arlas-tagger stack"
export ARLAS_SERVER_NODE=""
export ELASTIC_DATADIR="/tmp"
export KAFKA_DATADIR="/tmp"
docker compose -f ${DOCKER_COMPOSE_ES} --project-name arlas up -d --build
echo "Waiting for ES readiness"
docker run --net arlas_default --rm busybox sh -c 'i=1; until nc -w 2 elasticsearch 9200; do if [ $i -lt 30 ]; then sleep 1; else break; fi; i=$(($i + 1)); done'

docker compose -f ${DOCKER_COMPOSE_KAFKA} --project-name arlas up -d --build
echo "Waiting for KAFKA readiness"
docker run --net arlas_default --rm busybox sh -c 'i=1; until nc -w 2 kafka 29092; do if [ $i -lt 60 ]; then sleep 1; else break; fi; i=$(($i + 1)); done'

docker compose -f ${DOCKER_COMPOSE_TAGGER} --project-name arlas up -d --build

DOCKER_IP=$(docker-machine ip || echo "localhost")

echo "=> Wait for arlas-tagger up and running"
i=1; until nc -w 2 ${DOCKER_IP} 19998; do if [ $i -lt 30 ]; then sleep 1; else break; fi; i=$(($i + 1)); done

echo "=> Get swagger documentation"
mkdir -p target/tmp || echo "target/tmp exists"
i=1; until curl -XGET http://${DOCKER_IP}:19998/arlas_tagger/openapi.json -o target/tmp/openapi.json; do if [ $i -lt 60 ]; then sleep 1; else break; fi; i=$(($i + 1)); done
i=1; until curl -XGET http://${DOCKER_IP}:19998/arlas_tagger/openapi.yaml -o target/tmp/openapi.yaml; do if [ $i -lt 60 ]; then sleep 1; else break; fi; i=$(($i + 1)); done

mkdir -p openapi
cp target/tmp/openapi.yaml openapi
cp target/tmp/openapi.json openapi

echo "=> Stop arlas-tagger stack"
docker compose -f ${DOCKER_COMPOSE_TAGGER} -f ${DOCKER_COMPOSE_ES} -f ${DOCKER_COMPOSE_KAFKA} --project-name arlas down -v

echo "=> Generate API documentation"
mkdir -p docs/api
docker run --rm \
    --mount dst=/input/api.json,src="$PWD/openapi/openapi.json",type=bind,ro \
    --mount dst=/input/env.json,src="$PWD/conf/doc/widdershins.json",type=bind,ro \
    --mount dst=/output,src="$PWD/docs/api",type=bind \
	gisaia/widdershins:4.0.1

itests() {
	echo "=> Run integration tests"
    ./scripts/tests-integration.sh
}
if [ "$TESTS" == "YES" ]; then itests; else echo "=> Skip integration tests"; fi


#########################################
#### Generate API clients ###############
#########################################

if [ "$SKIP_API" == "YES" ]; then
  echo "=> Skipping generation of API clients"
else
  echo "=> Generate API clients"
  ls target/tmp/

  mkdir -p target/tmp/typescript-fetch
  docker run --rm \
      -e GROUP_ID="$(id -g)" \
      -e USER_ID="$(id -u)" \
      --mount dst=/input/api.json,src="$PWD/target/tmp/openapi.json",type=bind,ro \
      --mount dst=/output,src="$PWD/target/tmp/typescript-fetch",type=bind \
    gisaia/swagger-codegen-3.0.42 \
          -l typescript-fetch --additional-properties modelPropertyNaming=snake_case

  echo "=> Build Typescript API "${FULL_API_VERSION}
  cd ${BASEDIR}/target/tmp/typescript-fetch/
  cp ${BASEDIR}/conf/npm/package-build.json package.json
  cp ${BASEDIR}/conf/npm/tsconfig-build.json .
  npm version --no-git-tag-version ${FULL_API_VERSION}
  npm install
  npm run build-release
  npm run postbuild
  cd ${BASEDIR}

  echo "=> Publish Typescript API "
  cp ${BASEDIR}/conf/npm/package-publish.json ${BASEDIR}/target/tmp/typescript-fetch/dist/package.json
  cd ${BASEDIR}/target/tmp/typescript-fetch/dist
  npm version --no-git-tag-version ${FULL_API_VERSION}


  if [ "$RELEASE" == "YES" ]; then
      npm publish || echo "Publishing on npm failed ... continue ..."
  else echo "=> Skip npm api publish"; fi

fi

cd ${BASEDIR}

if [ "$RELEASE" == "YES" ]; then
    echo "=> Tag arlas-tagger docker image"
    docker tag gisaia/arlas-tagger:${ARLAS_TAGGER_VERSION} gisaia/arlas-tagger:latest
    echo "=> Push arlas-tagger docker image"
    docker push gisaia/arlas-tagger:${ARLAS_TAGGER_VERSION}
    docker push gisaia/arlas-tagger:latest
else echo "=> Skip docker push image"; fi

if [ "$RELEASE" == "YES" ]; then
    echo "=> Generate CHANGELOG.md"
    git tag v${ARLAS_TAGGER_VERSION}
    git push origin v${ARLAS_TAGGER_VERSION}
    #@see scripts/build-github-changelog-generator.sh in ARLAS-server project if you need a fresher version of this tool
    docker run -it --rm -v "$(pwd)":/usr/local/src/your-app gisaia/github-changelog-generator:latest github_changelog_generator \
        -u gisaia -p ARLAS-tagger --token ${GITHUB_CHANGELOG_TOKEN} \
        --no-pr-wo-labels --no-issues-wo-labels --no-unreleased --issue-line-labels API,OGC,conf,security,documentation \
        --exclude-labels type:duplicate,type:question,type:wontfix,type:invalid \
        --bug-labels type:bug \
        --enhancement-labels  type:enhancement \
        --breaking-labels type:breaking \
        --enhancement-label "**New stuff:**" --issues-label "**Miscellaneous:**" --since-tag v0.0.1
    git tag -d v${ARLAS_TAGGER_VERSION}
    git push origin :v${ARLAS_TAGGER_VERSION}
    echo "=> Commit release version"
    git add docs/api
    git add openapi/openapi.json
    git add openapi/openapi.yaml
    git commit -a -m "release version ${ARLAS_TAGGER_VERSION}"
    git tag v${ARLAS_TAGGER_VERSION}
    git push origin v${ARLAS_TAGGER_VERSION}
    git push origin develop

    echo "=> Merge develop into master"
    git checkout master
    git pull origin master
    git merge origin/develop
    git push origin master

    echo "=> Rebase develop"
    git checkout develop
    git pull origin develop
    git rebase origin/master
else echo "=> Skip git push master"; fi

echo "=> Update project version for develop"
mvn versions:set -DnewVersion=${ARLAS_DEV_VERSION}-SNAPSHOT

echo "=> Update REST API version in JAVA source code"
sed -i.bak 's/\"'${FULL_API_VERSION}'\"/\"API_VERSION\"/' arlas-tagger-rest/src/main/java/io/arlas/tagger/rest/tag/TagRESTService.java

if [ "$RELEASE" == "YES" ]; then
    sed -i.bak 's/\"'${FULL_API_VERSION}'\"/\"'${API_DEV_VERSION}-SNAPSHOT'\"/' openapi/openapi.yaml
    sed -i.bak 's/\"'${FULL_API_VERSION}'\"/\"'${API_DEV_VERSION}-SNAPSHOT'\"/' openapi/openapi.json
    git add openapi/openapi.json
    git add openapi/openapi.yaml
    git commit -a -m "development version ${ARLAS_DEV_VERSION}-SNAPSHOT"
    git push origin develop
    send_chat_message "Release of ARLAS-tagger, version ${ARLAS_permissions_VERSION}"
    send_chat_message "${RELEASE_COMMAND_LINE}"
else echo "=> Skip git push develop"; fi