<!-- Generator: Widdershins v4.0.1 -->

<h1 id="arlas-tagger-apis">ARLAS Tagger APIs vAPI_VERSION</h1>

> Scroll down for example requests and responses.

(Un)Tag fields of ARLAS collections

Base URLs:

* <a href="/arlas_tagger">/arlas_tagger</a>

Email: <a href="mailto:contact@gisaia.com">Gisaia</a> Web: <a href="http://www.gisaia.com/">Gisaia</a> 
License: <a href="https://www.apache.org/licenses/LICENSE-2.0.html">Apache 2.0</a>

<h1 id="arlas-tagger-apis-write">write</h1>

Tagger API

## Tag

<a id="opIdtagPost"></a>

`POST /write/{collection}/_tag`

Search and tag the elements found in the collection, given the filters

> Body parameter

```json
{
  "partitionFilter": [
    {
      "f": [
        [
          {
            "field": "string",
            "op": "eq",
            "value": "string"
          }
        ]
      ],
      "q": [
        [
          "string"
        ]
      ],
      "dateformat": "string",
      "righthand": true
    }
  ],
  "filter": {
    "f": [
      [
        {
          "field": "string",
          "op": "eq",
          "value": "string"
        }
      ]
    ],
    "q": [
      [
        "string"
      ]
    ],
    "dateformat": "string",
    "righthand": true
  },
  "form": {
    "pretty": true,
    "flat": true
  },
  "search": {
    "partitionFilter": [
      {
        "f": [
          [
            {
              "field": "string",
              "op": "eq",
              "value": "string"
            }
          ]
        ],
        "q": [
          [
            "string"
          ]
        ],
        "dateformat": "string",
        "righthand": true
      }
    ],
    "filter": {
      "f": [
        [
          {
            "field": "string",
            "op": "eq",
            "value": "string"
          }
        ]
      ],
      "q": [
        [
          "string"
        ]
      ],
      "dateformat": "string",
      "righthand": true
    },
    "form": {
      "pretty": true,
      "flat": true
    },
    "page": {
      "size": 0,
      "from": 0,
      "sort": "string",
      "after": "string",
      "before": "string"
    },
    "projection": {
      "includes": "string",
      "excludes": "string"
    },
    "returned_geometries": "string"
  },
  "tag": {
    "path": "string",
    "value": {}
  },
  "propagation": {
    "field": "string",
    "filter": {
      "f": [
        [
          {
            "field": "string",
            "op": "eq",
            "value": "string"
          }
        ]
      ],
      "q": [
        [
          "string"
        ]
      ],
      "dateformat": "string",
      "righthand": true
    }
  },
  "label": "string"
}
```

<h3 id="tag-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|collection|path|string|true|collection|
|pretty|query|boolean|false|Pretty print|
|body|body|[TagRequest](#schematagrequest)|false|none|

> Example responses

> 200 Response

```json
{
  "id": "string",
  "label": "string",
  "action": "ADD",
  "failures": [
    {
      "id": "string",
      "message": "string",
      "type": "string"
    }
  ],
  "failed": 0,
  "updated": 0,
  "progress": 0.1,
  "nbRequest": 0,
  "propagated": 0,
  "startTime": 0,
  "endTime": 0,
  "processingTimeMs": 0
}
```

<h3 id="tag-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|Successful operation|[UpdateResponse](#schemaupdateresponse)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad request.|[Error](#schemaerror)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Arlas Server Error.|[Error](#schemaerror)|

<aside class="success">
This operation does not require authentication
</aside>

## TagReplay

<a id="opIdtagReplay"></a>

`POST /write/{collection}/_tagreplay`

Scan the tagref topic and replay tagging operations from the given offset

<h3 id="tagreplay-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|collection|path|string|true|collection|
|offset|query|integer(int64)|true|The offset from which the replay must be done.|
|pretty|query|boolean|false|Pretty print|

> Example responses

> 200 Response

```json
0
```

<h3 id="tagreplay-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|Successful operation|integer|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad request.|[Error](#schemaerror)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Arlas Server Error.|[Error](#schemaerror)|

<aside class="success">
This operation does not require authentication
</aside>

## Untag

<a id="opIduntagPost"></a>

`POST /write/{collection}/_untag`

Search and untag the elements found in the collection, given the filters

> Body parameter

```json
{
  "partitionFilter": [
    {
      "f": [
        [
          {
            "field": "string",
            "op": "eq",
            "value": "string"
          }
        ]
      ],
      "q": [
        [
          "string"
        ]
      ],
      "dateformat": "string",
      "righthand": true
    }
  ],
  "filter": {
    "f": [
      [
        {
          "field": "string",
          "op": "eq",
          "value": "string"
        }
      ]
    ],
    "q": [
      [
        "string"
      ]
    ],
    "dateformat": "string",
    "righthand": true
  },
  "form": {
    "pretty": true,
    "flat": true
  },
  "search": {
    "partitionFilter": [
      {
        "f": [
          [
            {
              "field": "string",
              "op": "eq",
              "value": "string"
            }
          ]
        ],
        "q": [
          [
            "string"
          ]
        ],
        "dateformat": "string",
        "righthand": true
      }
    ],
    "filter": {
      "f": [
        [
          {
            "field": "string",
            "op": "eq",
            "value": "string"
          }
        ]
      ],
      "q": [
        [
          "string"
        ]
      ],
      "dateformat": "string",
      "righthand": true
    },
    "form": {
      "pretty": true,
      "flat": true
    },
    "page": {
      "size": 0,
      "from": 0,
      "sort": "string",
      "after": "string",
      "before": "string"
    },
    "projection": {
      "includes": "string",
      "excludes": "string"
    },
    "returned_geometries": "string"
  },
  "tag": {
    "path": "string",
    "value": {}
  },
  "propagation": {
    "field": "string",
    "filter": {
      "f": [
        [
          {
            "field": "string",
            "op": "eq",
            "value": "string"
          }
        ]
      ],
      "q": [
        [
          "string"
        ]
      ],
      "dateformat": "string",
      "righthand": true
    }
  },
  "label": "string"
}
```

<h3 id="untag-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|collection|path|string|true|collection|
|pretty|query|boolean|false|Pretty print|
|body|body|[TagRequest](#schematagrequest)|false|none|

> Example responses

> 200 Response

```json
{
  "id": "string",
  "label": "string",
  "action": "ADD",
  "failures": [
    {
      "id": "string",
      "message": "string",
      "type": "string"
    }
  ],
  "failed": 0,
  "updated": 0,
  "progress": 0.1,
  "nbRequest": 0,
  "propagated": 0,
  "startTime": 0,
  "endTime": 0,
  "processingTimeMs": 0
}
```

<h3 id="untag-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|Successful operation|[UpdateResponse](#schemaupdateresponse)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad request.|[Error](#schemaerror)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Arlas Server Error.|[Error](#schemaerror)|

<aside class="success">
This operation does not require authentication
</aside>

<h1 id="arlas-tagger-apis-status">status</h1>

Tagger status API

## TagStatus

<a id="opIdtaggingGet"></a>

`GET /status/{collection}/_tag/{id}`

Get the status of the (un)tagging operation, given the id of a previously requested operation

<h3 id="tagstatus-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|collection|path|string|true|collection|
|id|path|string|true|The id of a previously requested (un)tag operation.|
|pretty|query|boolean|false|Pretty print|

> Example responses

> 200 Response

```json
{
  "id": "string",
  "label": "string",
  "action": "ADD",
  "failures": [
    {
      "id": "string",
      "message": "string",
      "type": "string"
    }
  ],
  "failed": 0,
  "updated": 0,
  "progress": 0.1,
  "nbRequest": 0,
  "propagated": 0,
  "startTime": 0,
  "endTime": 0,
  "processingTimeMs": 0
}
```

<h3 id="tagstatus-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|Successful operation|[UpdateResponse](#schemaupdateresponse)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad request.|[Error](#schemaerror)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Arlas Server Error.|[Error](#schemaerror)|

<aside class="success">
This operation does not require authentication
</aside>

## TagList

<a id="opIdtaggingGetList"></a>

`GET /status/{collection}/_taglist`

Get the list of previously submitted tag requests

<h3 id="taglist-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|collection|path|string|true|collection|
|pretty|query|boolean|false|Pretty print|

> Example responses

> 200 Response

```json
[
  {
    "partitionFilter": "string",
    "filter": {
      "f": [
        [
          {
            "field": "string",
            "op": "eq",
            "value": "string"
          }
        ]
      ],
      "q": [
        [
          "string"
        ]
      ],
      "dateformat": "string",
      "righthand": true
    },
    "form": {
      "pretty": true,
      "flat": true
    },
    "search": {
      "partitionFilter": [
        {
          "f": [
            [
              {
                "field": "string",
                "op": "eq",
                "value": "string"
              }
            ]
          ],
          "q": [
            [
              "string"
            ]
          ],
          "dateformat": "string",
          "righthand": true
        }
      ],
      "filter": {
        "f": [
          [
            {
              "field": "string",
              "op": "eq",
              "value": "string"
            }
          ]
        ],
        "q": [
          [
            "string"
          ]
        ],
        "dateformat": "string",
        "righthand": true
      },
      "form": {
        "pretty": true,
        "flat": true
      },
      "page": {
        "size": 0,
        "from": 0,
        "sort": "string",
        "after": "string",
        "before": "string"
      },
      "projection": {
        "includes": "string",
        "excludes": "string"
      },
      "returned_geometries": "string"
    },
    "tag": {
      "path": "string",
      "value": {}
    },
    "propagation": {
      "field": "string",
      "filter": {
        "f": [
          [
            {
              "field": "string",
              "op": "eq",
              "value": "string"
            }
          ]
        ],
        "q": [
          [
            "string"
          ]
        ],
        "dateformat": "string",
        "righthand": true
      }
    },
    "label": "string",
    "id": "string",
    "action": "ADD",
    "collection": "string",
    "propagated": 0,
    "creationTime": 0,
    "offset": 0
  }
]
```

<h3 id="taglist-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|Successful operation|Inline|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad request.|[Error](#schemaerror)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Arlas Server Error.|[Error](#schemaerror)|

<h3 id="taglist-responseschema">Response Schema</h3>

Status Code **200**

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|*anonymous*|[[TagRefRequest](#schematagrefrequest)]|false|none|none|
|» partitionFilter|string|false|none|none|
|» filter|[Filter](#schemafilter)|false|none|none|
|»» f|[array]|false|none|none|
|»»» field|string|false|none|none|
|»»» op|string|false|none|none|
|»»» value|string|false|none|none|
|»»» empty|boolean|false|none|none|
|q|[array]|false|none|none|
|» empty|boolean|false|none|none|
|dateformat|string|false|none|none|
|righthand|boolean|false|none|none|
|form|[Form](#schemaform)|false|none|none|
|» pretty|boolean|false|none|none|
|» flat|boolean|false|none|none|
|search|[Search](#schemasearch)|false|none|none|
|» partitionFilter|[[Filter](#schemafilter)]|false|none|none|
|» filter|[Filter](#schemafilter)|false|none|none|
|» form|[Form](#schemaform)|false|none|none|
|» page|[Page](#schemapage)|false|none|none|
|»» size|integer(int32)|false|none|none|
|»» from|integer(int32)|false|none|none|
|»» sort|string|false|none|none|
|»» after|string|false|none|none|
|»» before|string|false|none|none|
|» projection|[Projection](#schemaprojection)|false|none|none|
|»» includes|string|false|none|none|
|»» excludes|string|false|none|none|
|» returned_geometries|string|false|none|none|
|tag|[Tag](#schematag)|false|none|none|
|» path|string|false|none|none|
|» value|object|false|none|none|
|propagation|[Propagation](#schemapropagation)|false|none|none|
|» field|string|false|none|none|
|» filter|[Filter](#schemafilter)|false|none|none|
|label|string|false|none|none|
|id|string|false|none|none|
|action|string|false|none|none|
|collection|string|false|none|none|
|propagated|integer(int64)|false|none|none|
|creationTime|integer(int64)|false|none|none|
|offset|integer(int64)|false|none|none|

#### Enumerated Values

|Property|Value|
|---|---|
|op|eq|
|op|gte|
|op|gt|
|op|lte|
|op|lt|
|op|like|
|op|ne|
|op|range|
|op|within|
|op|notwithin|
|op|intersects|
|op|notintersects|
|action|ADD|
|action|REMOVE|
|action|REMOVEALL|

<aside class="success">
This operation does not require authentication
</aside>

# Schemas

<h2 id="tocS_Failure">Failure</h2>
<!-- backwards compatibility -->
<a id="schemafailure"></a>
<a id="schema_Failure"></a>
<a id="tocSfailure"></a>
<a id="tocsfailure"></a>

```json
{
  "id": "string",
  "message": "string",
  "type": "string"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|string|false|none|none|
|message|string|false|none|none|
|type|string|false|none|none|

<h2 id="tocS_UpdateResponse">UpdateResponse</h2>
<!-- backwards compatibility -->
<a id="schemaupdateresponse"></a>
<a id="schema_UpdateResponse"></a>
<a id="tocSupdateresponse"></a>
<a id="tocsupdateresponse"></a>

```json
{
  "id": "string",
  "label": "string",
  "action": "ADD",
  "failures": [
    {
      "id": "string",
      "message": "string",
      "type": "string"
    }
  ],
  "failed": 0,
  "updated": 0,
  "progress": 0.1,
  "nbRequest": 0,
  "propagated": 0,
  "startTime": 0,
  "endTime": 0,
  "processingTimeMs": 0
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|string|false|none|none|
|label|string|false|none|none|
|action|string|false|none|none|
|failures|[[Failure](#schemafailure)]|false|none|none|
|failed|integer(int64)|false|none|none|
|updated|integer(int64)|false|none|none|
|progress|number(float)|false|none|none|
|nbRequest|integer(int64)|false|none|none|
|propagated|integer(int64)|false|none|none|
|startTime|integer(int64)|false|none|none|
|endTime|integer(int64)|false|none|none|
|processingTimeMs|integer(int64)|false|none|none|

#### Enumerated Values

|Property|Value|
|---|---|
|action|ADD|
|action|REMOVE|
|action|REMOVEALL|

<h2 id="tocS_Error">Error</h2>
<!-- backwards compatibility -->
<a id="schemaerror"></a>
<a id="schema_Error"></a>
<a id="tocSerror"></a>
<a id="tocserror"></a>

```json
{
  "status": 0,
  "message": "string",
  "error": "string"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|status|integer(int32)|false|none|none|
|message|string|false|none|none|
|error|string|false|none|none|

<h2 id="tocS_Expression">Expression</h2>
<!-- backwards compatibility -->
<a id="schemaexpression"></a>
<a id="schema_Expression"></a>
<a id="tocSexpression"></a>
<a id="tocsexpression"></a>

```json
{
  "field": "string",
  "op": "eq",
  "value": "string"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|field|string|false|none|none|
|op|string|false|none|none|
|value|string|false|none|none|

#### Enumerated Values

|Property|Value|
|---|---|
|op|eq|
|op|gte|
|op|gt|
|op|lte|
|op|lt|
|op|like|
|op|ne|
|op|range|
|op|within|
|op|notwithin|
|op|intersects|
|op|notintersects|

<h2 id="tocS_Filter">Filter</h2>
<!-- backwards compatibility -->
<a id="schemafilter"></a>
<a id="schema_Filter"></a>
<a id="tocSfilter"></a>
<a id="tocsfilter"></a>

```json
{
  "f": [
    [
      {
        "field": "string",
        "op": "eq",
        "value": "string"
      }
    ]
  ],
  "q": [
    [
      "string"
    ]
  ],
  "dateformat": "string",
  "righthand": true
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|f|[array]|false|none|none|
|» empty|boolean|false|none|none|
|q|[array]|false|none|none|
|» empty|boolean|false|none|none|
|dateformat|string|false|none|none|
|righthand|boolean|false|none|none|

<h2 id="tocS_Form">Form</h2>
<!-- backwards compatibility -->
<a id="schemaform"></a>
<a id="schema_Form"></a>
<a id="tocSform"></a>
<a id="tocsform"></a>

```json
{
  "pretty": true,
  "flat": true
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|pretty|boolean|false|none|none|
|flat|boolean|false|none|none|

<h2 id="tocS_MultiValueFilterExpression">MultiValueFilterExpression</h2>
<!-- backwards compatibility -->
<a id="schemamultivaluefilterexpression"></a>
<a id="schema_MultiValueFilterExpression"></a>
<a id="tocSmultivaluefilterexpression"></a>
<a id="tocsmultivaluefilterexpression"></a>

```json
[
  {
    "field": "string",
    "op": "eq",
    "value": "string"
  }
]

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|*anonymous*|[[Expression](#schemaexpression)]|false|none|none|
|empty|boolean|false|none|none|

<h2 id="tocS_MultiValueFilterString">MultiValueFilterString</h2>
<!-- backwards compatibility -->
<a id="schemamultivaluefilterstring"></a>
<a id="schema_MultiValueFilterString"></a>
<a id="tocSmultivaluefilterstring"></a>
<a id="tocsmultivaluefilterstring"></a>

```json
[
  "string"
]

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|empty|boolean|false|none|none|

<h2 id="tocS_Page">Page</h2>
<!-- backwards compatibility -->
<a id="schemapage"></a>
<a id="schema_Page"></a>
<a id="tocSpage"></a>
<a id="tocspage"></a>

```json
{
  "size": 0,
  "from": 0,
  "sort": "string",
  "after": "string",
  "before": "string"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|size|integer(int32)|false|none|none|
|from|integer(int32)|false|none|none|
|sort|string|false|none|none|
|after|string|false|none|none|
|before|string|false|none|none|

<h2 id="tocS_Projection">Projection</h2>
<!-- backwards compatibility -->
<a id="schemaprojection"></a>
<a id="schema_Projection"></a>
<a id="tocSprojection"></a>
<a id="tocsprojection"></a>

```json
{
  "includes": "string",
  "excludes": "string"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|includes|string|false|none|none|
|excludes|string|false|none|none|

<h2 id="tocS_Propagation">Propagation</h2>
<!-- backwards compatibility -->
<a id="schemapropagation"></a>
<a id="schema_Propagation"></a>
<a id="tocSpropagation"></a>
<a id="tocspropagation"></a>

```json
{
  "field": "string",
  "filter": {
    "f": [
      [
        {
          "field": "string",
          "op": "eq",
          "value": "string"
        }
      ]
    ],
    "q": [
      [
        "string"
      ]
    ],
    "dateformat": "string",
    "righthand": true
  }
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|field|string|false|none|none|
|filter|[Filter](#schemafilter)|false|none|none|

<h2 id="tocS_Search">Search</h2>
<!-- backwards compatibility -->
<a id="schemasearch"></a>
<a id="schema_Search"></a>
<a id="tocSsearch"></a>
<a id="tocssearch"></a>

```json
{
  "partitionFilter": [
    {
      "f": [
        [
          {
            "field": "string",
            "op": "eq",
            "value": "string"
          }
        ]
      ],
      "q": [
        [
          "string"
        ]
      ],
      "dateformat": "string",
      "righthand": true
    }
  ],
  "filter": {
    "f": [
      [
        {
          "field": "string",
          "op": "eq",
          "value": "string"
        }
      ]
    ],
    "q": [
      [
        "string"
      ]
    ],
    "dateformat": "string",
    "righthand": true
  },
  "form": {
    "pretty": true,
    "flat": true
  },
  "page": {
    "size": 0,
    "from": 0,
    "sort": "string",
    "after": "string",
    "before": "string"
  },
  "projection": {
    "includes": "string",
    "excludes": "string"
  },
  "returned_geometries": "string"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|partitionFilter|[[Filter](#schemafilter)]|false|none|none|
|filter|[Filter](#schemafilter)|false|none|none|
|form|[Form](#schemaform)|false|none|none|
|page|[Page](#schemapage)|false|none|none|
|projection|[Projection](#schemaprojection)|false|none|none|
|returned_geometries|string|false|none|none|

<h2 id="tocS_Tag">Tag</h2>
<!-- backwards compatibility -->
<a id="schematag"></a>
<a id="schema_Tag"></a>
<a id="tocStag"></a>
<a id="tocstag"></a>

```json
{
  "path": "string",
  "value": {}
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|path|string|false|none|none|
|value|object|false|none|none|

<h2 id="tocS_TagRequest">TagRequest</h2>
<!-- backwards compatibility -->
<a id="schematagrequest"></a>
<a id="schema_TagRequest"></a>
<a id="tocStagrequest"></a>
<a id="tocstagrequest"></a>

```json
{
  "partitionFilter": [
    {
      "f": [
        [
          {
            "field": "string",
            "op": "eq",
            "value": "string"
          }
        ]
      ],
      "q": [
        [
          "string"
        ]
      ],
      "dateformat": "string",
      "righthand": true
    }
  ],
  "filter": {
    "f": [
      [
        {
          "field": "string",
          "op": "eq",
          "value": "string"
        }
      ]
    ],
    "q": [
      [
        "string"
      ]
    ],
    "dateformat": "string",
    "righthand": true
  },
  "form": {
    "pretty": true,
    "flat": true
  },
  "search": {
    "partitionFilter": [
      {
        "f": [
          [
            {
              "field": "string",
              "op": "eq",
              "value": "string"
            }
          ]
        ],
        "q": [
          [
            "string"
          ]
        ],
        "dateformat": "string",
        "righthand": true
      }
    ],
    "filter": {
      "f": [
        [
          {
            "field": "string",
            "op": "eq",
            "value": "string"
          }
        ]
      ],
      "q": [
        [
          "string"
        ]
      ],
      "dateformat": "string",
      "righthand": true
    },
    "form": {
      "pretty": true,
      "flat": true
    },
    "page": {
      "size": 0,
      "from": 0,
      "sort": "string",
      "after": "string",
      "before": "string"
    },
    "projection": {
      "includes": "string",
      "excludes": "string"
    },
    "returned_geometries": "string"
  },
  "tag": {
    "path": "string",
    "value": {}
  },
  "propagation": {
    "field": "string",
    "filter": {
      "f": [
        [
          {
            "field": "string",
            "op": "eq",
            "value": "string"
          }
        ]
      ],
      "q": [
        [
          "string"
        ]
      ],
      "dateformat": "string",
      "righthand": true
    }
  },
  "label": "string"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|partitionFilter|[[Filter](#schemafilter)]|false|none|none|
|filter|[Filter](#schemafilter)|false|none|none|
|form|[Form](#schemaform)|false|none|none|
|search|[Search](#schemasearch)|false|none|none|
|tag|[Tag](#schematag)|false|none|none|
|propagation|[Propagation](#schemapropagation)|false|none|none|
|label|string|false|none|none|

<h2 id="tocS_TagRefRequest">TagRefRequest</h2>
<!-- backwards compatibility -->
<a id="schematagrefrequest"></a>
<a id="schema_TagRefRequest"></a>
<a id="tocStagrefrequest"></a>
<a id="tocstagrefrequest"></a>

```json
{
  "partitionFilter": "string",
  "filter": {
    "f": [
      [
        {
          "field": "string",
          "op": "eq",
          "value": "string"
        }
      ]
    ],
    "q": [
      [
        "string"
      ]
    ],
    "dateformat": "string",
    "righthand": true
  },
  "form": {
    "pretty": true,
    "flat": true
  },
  "search": {
    "partitionFilter": [
      {
        "f": [
          [
            {
              "field": "string",
              "op": "eq",
              "value": "string"
            }
          ]
        ],
        "q": [
          [
            "string"
          ]
        ],
        "dateformat": "string",
        "righthand": true
      }
    ],
    "filter": {
      "f": [
        [
          {
            "field": "string",
            "op": "eq",
            "value": "string"
          }
        ]
      ],
      "q": [
        [
          "string"
        ]
      ],
      "dateformat": "string",
      "righthand": true
    },
    "form": {
      "pretty": true,
      "flat": true
    },
    "page": {
      "size": 0,
      "from": 0,
      "sort": "string",
      "after": "string",
      "before": "string"
    },
    "projection": {
      "includes": "string",
      "excludes": "string"
    },
    "returned_geometries": "string"
  },
  "tag": {
    "path": "string",
    "value": {}
  },
  "propagation": {
    "field": "string",
    "filter": {
      "f": [
        [
          {
            "field": "string",
            "op": "eq",
            "value": "string"
          }
        ]
      ],
      "q": [
        [
          "string"
        ]
      ],
      "dateformat": "string",
      "righthand": true
    }
  },
  "label": "string",
  "id": "string",
  "action": "ADD",
  "collection": "string",
  "propagated": 0,
  "creationTime": 0,
  "offset": 0
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|partitionFilter|string|false|none|none|
|filter|[Filter](#schemafilter)|false|none|none|
|form|[Form](#schemaform)|false|none|none|
|search|[Search](#schemasearch)|false|none|none|
|tag|[Tag](#schematag)|false|none|none|
|propagation|[Propagation](#schemapropagation)|false|none|none|
|label|string|false|none|none|
|id|string|false|none|none|
|action|string|false|none|none|
|collection|string|false|none|none|
|propagated|integer(int64)|false|none|none|
|creationTime|integer(int64)|false|none|none|
|offset|integer(int64)|false|none|none|

#### Enumerated Values

|Property|Value|
|---|---|
|action|ADD|
|action|REMOVE|
|action|REMOVEALL|

