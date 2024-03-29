
<a name="definitions"></a>
## Definitions

<a name="error"></a>
### Error

|Name|Schema|
|---|---|
|**error**  <br>*optional*|string|
|**message**  <br>*optional*|string|
|**status**  <br>*optional*|integer (int32)|


<a name="expression"></a>
### Expression

|Name|Schema|
|---|---|
|**field**  <br>*optional*|string|
|**op**  <br>*optional*|enum (eq, gte, gt, lte, lt, like, ne, range, within, notwithin, intersects, notintersects)|
|**value**  <br>*optional*|string|


<a name="failure"></a>
### Failure

|Name|Schema|
|---|---|
|**id**  <br>*optional*|string|
|**message**  <br>*optional*|string|
|**type**  <br>*optional*|string|


<a name="filter"></a>
### Filter

|Name|Schema|
|---|---|
|**dateformat**  <br>*optional*|string|
|**f**  <br>*optional*|< < [Expression](#expression) > array > array|
|**q**  <br>*optional*|< < string > array > array|
|**righthand**  <br>*optional*|boolean|


<a name="form"></a>
### Form

|Name|Schema|
|---|---|
|**flat**  <br>*optional*|boolean|
|**pretty**  <br>*optional*|boolean|


<a name="page"></a>
### Page

|Name|Schema|
|---|---|
|**after**  <br>*optional*|string|
|**before**  <br>*optional*|string|
|**from**  <br>*optional*|integer (int32)|
|**size**  <br>*optional*|integer (int32)|
|**sort**  <br>*optional*|string|


<a name="projection"></a>
### Projection

|Name|Schema|
|---|---|
|**excludes**  <br>*optional*|string|
|**includes**  <br>*optional*|string|


<a name="propagation"></a>
### Propagation

|Name|Schema|
|---|---|
|**field**  <br>*optional*|string|
|**filter**  <br>*optional*|[Filter](#filter)|


<a name="search"></a>
### Search

|Name|Schema|
|---|---|
|**filter**  <br>*optional*|[Filter](#filter)|
|**form**  <br>*optional*|[Form](#form)|
|**page**  <br>*optional*|[Page](#page)|
|**projection**  <br>*optional*|[Projection](#projection)|
|**returned_geometries**  <br>*optional*|string|


<a name="tag"></a>
### Tag

|Name|Schema|
|---|---|
|**path**  <br>*optional*|string|
|**value**  <br>*optional*|object|


<a name="tagrefrequest"></a>
### TagRefRequest

|Name|Schema|
|---|---|
|**action**  <br>*optional*|enum (ADD, REMOVE, REMOVEALL)|
|**collection**  <br>*optional*|string|
|**creationTime**  <br>*optional*|integer (int64)|
|**filter**  <br>*optional*|[Filter](#filter)|
|**form**  <br>*optional*|[Form](#form)|
|**id**  <br>*optional*|string|
|**label**  <br>*optional*|string|
|**offset**  <br>*optional*|integer (int64)|
|**partitionFilter**  <br>*optional*|string|
|**propagated**  <br>*optional*|integer (int64)|
|**propagation**  <br>*optional*|[Propagation](#propagation)|
|**search**  <br>*optional*|[Search](#search)|
|**tag**  <br>*optional*|[Tag](#tag)|


<a name="tagrequest"></a>
### TagRequest

|Name|Schema|
|---|---|
|**filter**  <br>*optional*|[Filter](#filter)|
|**form**  <br>*optional*|[Form](#form)|
|**label**  <br>*optional*|string|
|**propagation**  <br>*optional*|[Propagation](#propagation)|
|**search**  <br>*optional*|[Search](#search)|
|**tag**  <br>*optional*|[Tag](#tag)|


<a name="updateresponse"></a>
### UpdateResponse

|Name|Schema|
|---|---|
|**action**  <br>*optional*|enum (ADD, REMOVE, REMOVEALL)|
|**endTime**  <br>*optional*|integer (int64)|
|**failed**  <br>*optional*|integer (int64)|
|**failures**  <br>*optional*|< [Failure](#failure) > array|
|**id**  <br>*optional*|string|
|**label**  <br>*optional*|string|
|**nbRequest**  <br>*optional*|integer (int64)|
|**processingTimeMs**  <br>*optional*|integer (int64)|
|**progress**  <br>*optional*|number (float)|
|**propagated**  <br>*optional*|integer (int64)|
|**startTime**  <br>*optional*|integer (int64)|
|**updated**  <br>*optional*|integer (int64)|



