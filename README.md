# ARLAS-tagger

[![Build Status](https://api.travis-ci.org/gisaia/ARLAS-tagger.svg?branch=develop)](https://travis-ci.org/gisaia/ARLAS-tagger)

ARLAS-tagger provides a **REST API** for tagging data collections available in *Elasticsearch* and referenced by [ARLAS-server](https://github.com/gisaia/ARLAS-server)

## Prerequisites :

### Building

ARLAS-tagger is a Dropwizard project. You need JDK 8 and Maven 3 to be installed.

### Running

You need a Java Runtime (JRE) 8 and a Kafka node running.

> Note : data collections tagged by ARLAS-tagger should be referenced in Elasticsearch by ARLAS-server

## Build

### JAR
In order to download the project dependencies and build it :

```sh
mvn clean package
```
### Docker

```sh
docker build --tag=gisaia/arlas-tagger:latest --tag=arlas-tagger:latest .
```

## Run

### JAR

To run the project :

```sh
java -jar target/arlas-tagger-x.x.jar server conf/configuration.yaml
```

Then, go to `http://localhost:9998/arlas_tagger/swagger` for testing the API.

### Docker

```sh
docker run -d -e KAFKA_BROKERS=my-host-1:9092 -e ARLAS_ELASTIC_NODES=my-host-2:9300 -e ARLAS_ELASTIC_CLUSTER=elasticsearch  gisaia/arlas-tagger:latest
```

## Running the tests
### Integration tests
#### with docker containers

```sh
./scripts/tests-integration.sh
```

Make sure to have docker installed and running on your system and you might need to install some dependencies :

```sh
sudo apt-get install xmlstarlet
```

Have a look to the [kafka image documentation](https://hub.docker.com/r/wurstmeister/kafka/) if you are in trouble with kafka container.

#### with running kafka, elasticsearch and ARLAS-tagger

```sh
export ARLAS_TAGGER_HOST="localhost"; export ARLAS_TAGGER_PORT="9998"; export ARLAS_TAGGER_PREFIX="/arlas/";
export ARLAS_ELASTIC_NODES="localhost:9300"; export KAFKA_BROKERS="localhost:9092";
mvn clean install -DskipTests=false
```

## Built with :

- [Dropwizard](http://www.dropwizard.io) - The web framework used.
- [Maven](https://maven.apache.org/) - Dependency Management.
- [Kafka](https://kafka.apache.org/) -  A distributed streaming platform.

## Contributing :

Please read [CONTRIBUTING.md](CONTRIBUTING.md) for details on our code of conduct, and the process for submitting pull requests to us.

## Versioning :

We use [SemVer](http://semver.org/) for versioning : `x.y.z`.

- `x` : Incremented as soon as an incompatible change is made to `ARLAS-tagger API`.
- `y` : Incremented as soon as a new feature is implemented.
- `z` : Incremented as soon as the `ARLAS-web-contributors` implementation receives a fix or an enhancement.

For the versions available, see the [releases](https://github.com/gisaia/ARLAS-tagger/releases) on this repository.

ARLAS-tagger is compliant with ARLAS-server releases that have the same major version.

## Authors :

- Gisaïa - *Initial work* - [Gisaïa](http://gisaia.fr/)

See also the list of [contributors](https://gitlab.com/GISAIA.ARLAS/ARLAS-tagger/graphs/develop) who participated in this project.

## License :

This project is licensed under the Apache License, Version 2.0 - see the [LICENSE.txt](LICENSE.txt) file for details.

## Acknowledgments :
This project has been initiated and is maintained by [Gisaïa](https://gisaia.com)