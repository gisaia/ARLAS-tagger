# ARLAS Tagger API Overview

The ARLAS Tagger offers 3 APIs:

- a `write` API for [tagging](arlas-api-tagging.md), meaning adding (or removing) a value to a field in ARLAS `collections` (`http://.../arlas_tagger/write/`)
- a `status` API for monitoring the `tagging` operation status (`http://.../arlas_tagger/status/`
- an API for monitoring the server health and performances
- endpoints for testing the write API and the status API with swagger

## Monitoring

The monitoring API provides some information about the health and the performances of the ARLAS Tagger that can be of interest:

| URL | Description |
| --- | --- |
| http://.../admin/metrics?pretty=true  |  Metrics about the performances of the ARLAS Tagger. Metrics about the `write` and `status` APIs are prefixed with `io.arlas.tagger`|
| http://.../admin/ping | Returns pong  |
| http://.../admin/threads | List of running threads |
| http://.../admin/healthcheck?pretty=true  |  Whether the service is healthy or not |


## Swagger

| URL | Description |
| --- | --- |
| http://.../arlas/swagger  | The web application for testing the API  |
| http://.../arlas/swagger.yaml  | The swagger definition of the collections/exploration API with YAML format |
| http://.../arlas/swagger.json  | The swagger definition of the collections/exploration API with JSON format |
