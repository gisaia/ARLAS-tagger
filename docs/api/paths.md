
<a name="paths"></a>
## Resources

<a name="status_resource"></a>
### Status

<a name="taggingget"></a>
#### TagStatus
```
GET /status/{collection}/_tag/{id}
```


##### Description
Get the status of the (un)tagging operation, given the id of a previously requested operation


##### Parameters

|Type|Name|Description|Schema|Default|
|---|---|---|---|---|
|**Path**|**collection**  <br>*required*|collection|string||
|**Path**|**id**  <br>*required*|The id of a previously requested (un)tag operation.|string||
|**Query**|**pretty**  <br>*optional*|Pretty print|boolean|`"false"`|


##### Responses

|HTTP Code|Description|Schema|
|---|---|---|
|**200**|Successful operation|[UpdateResponse](#updateresponse)|
|**400**|Bad request.|[Error](#error)|
|**500**|Arlas Server Error.|[Error](#error)|


##### Consumes

* `application/json;charset=utf-8`


##### Produces

* `application/json;charset=utf-8`


<a name="tagginggetlist"></a>
#### TagList
```
GET /status/{collection}/_taglist
```


##### Description
Get the list of previously submitted tag requests


##### Parameters

|Type|Name|Description|Schema|Default|
|---|---|---|---|---|
|**Path**|**collection**  <br>*required*|collection|string||
|**Query**|**pretty**  <br>*optional*|Pretty print|boolean|`"false"`|


##### Responses

|HTTP Code|Description|Schema|
|---|---|---|
|**200**|Successful operation|< [TagRefRequest](#tagrefrequest) > array|
|**400**|Bad request.|[Error](#error)|
|**500**|Arlas Server Error.|[Error](#error)|


##### Consumes

* `application/json;charset=utf-8`


##### Produces

* `application/json;charset=utf-8`


<a name="write_resource"></a>
### Write

<a name="tagpost"></a>
#### Tag
```
POST /write/{collection}/_tag
```


##### Description
Search and tag the elements found in the collection, given the filters


##### Parameters

|Type|Name|Description|Schema|Default|
|---|---|---|---|---|
|**Path**|**collection**  <br>*required*|collection|string||
|**Query**|**pretty**  <br>*optional*|Pretty print|boolean|`"false"`|
|**Body**|**body**  <br>*optional*||[TagRequest](#tagrequest)||


##### Responses

|HTTP Code|Description|Schema|
|---|---|---|
|**200**|Successful operation|[UpdateResponse](#updateresponse)|
|**400**|Bad request.|[Error](#error)|
|**500**|Arlas Server Error.|[Error](#error)|


##### Consumes

* `application/json;charset=utf-8`


##### Produces

* `application/json;charset=utf-8`


<a name="tagreplay"></a>
#### TagReplay
```
POST /write/{collection}/_tagreplay
```


##### Description
Scan the tagref topic and replay tagging operations from the given offset


##### Parameters

|Type|Name|Description|Schema|Default|
|---|---|---|---|---|
|**Path**|**collection**  <br>*required*|collection|string||
|**Query**|**offset**  <br>*required*|The offset from which the replay must be done.|integer (int64)||
|**Query**|**pretty**  <br>*optional*|Pretty print|boolean|`"false"`|


##### Responses

|HTTP Code|Description|Schema|
|---|---|---|
|**200**|Successful operation|integer (int64)|
|**400**|Bad request.|[Error](#error)|
|**500**|Arlas Server Error.|[Error](#error)|


##### Consumes

* `application/json;charset=utf-8`


##### Produces

* `application/json;charset=utf-8`


<a name="untagpost"></a>
#### Untag
```
POST /write/{collection}/_untag
```


##### Description
Search and untag the elements found in the collection, given the filters


##### Parameters

|Type|Name|Description|Schema|Default|
|---|---|---|---|---|
|**Path**|**collection**  <br>*required*|collection|string||
|**Query**|**pretty**  <br>*optional*|Pretty print|boolean|`"false"`|
|**Body**|**body**  <br>*optional*||[TagRequest](#tagrequest)||


##### Responses

|HTTP Code|Description|Schema|
|---|---|---|
|**200**|Successful operation|[UpdateResponse](#updateresponse)|
|**400**|Bad request.|[Error](#error)|
|**500**|Arlas Server Error.|[Error](#error)|


##### Consumes

* `application/json;charset=utf-8`


##### Produces

* `application/json;charset=utf-8`



