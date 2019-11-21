/*
 * Licensed to Gisaïa under one or more contributor
 * license agreements. See the NOTICE.txt file distributed with
 * this work for additional information regarding copyright
 * ownership. Gisaïa licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.arlas.tagger.rest.tag;

import io.arlas.tagger.AbstractTaggerTestContext;
import io.arlas.tagger.ArlasServerContext;
import io.arlas.tagger.CollectionTool;
import io.arlas.tagger.model.Tag;
import io.arlas.tagger.model.request.TagRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.HttpHeaders;
import java.io.IOException;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;

public class TagAuthIT extends AbstractTaggerTestContext {

//    "http://arlas.io/permissions": [
//            "rule:collections:GET:100",
//            "rule:explore/_list:GET:200",
//            "variable:organisation:axxes",
//            "rule:explore/${organisation}/_search:GET:300",
//            "rule:explore/axxes/_count:GET:200",
//            "rule:explore/axxes/_geoaggregate/.*:GET:200",
//            "rule:explore/axxes/_range:GET:200",
//            "rule:explore/axxes/_aggregate:GET:200",
//            "rule:write/geodata/_tag:POST:200"
//            ],
    private String token = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImtpZCI6Ik1FWXhNVEF3TTBORk9URXlSVVpDTlRRek0wVXhOVU0xUlVWQlF6TTNNVFpFT0VZNFJEWTBPUSJ9.eyJodHRwOi8vYXJsYXMuaW8vcGVybWlzc2lvbnMiOlsicnVsZTpjb2xsZWN0aW9uczpHRVQ6MTAwIiwicnVsZTpleHBsb3JlL19saXN0OkdFVDoyMDAiLCJ2YXJpYWJsZTpvcmdhbmlzYXRpb246YXh4ZXMiLCJydWxlOmV4cGxvcmUvJHtvcmdhbmlzYXRpb259L19zZWFyY2g6R0VUOjMwMCIsInJ1bGU6ZXhwbG9yZS9heHhlcy9fY291bnQ6R0VUOjIwMCIsInJ1bGU6ZXhwbG9yZS9heHhlcy9fZ2VvYWdncmVnYXRlLy4qOkdFVDoyMDAiLCJydWxlOmV4cGxvcmUvYXh4ZXMvX3JhbmdlOkdFVDoyMDAiLCJydWxlOmV4cGxvcmUvYXh4ZXMvX2FnZ3JlZ2F0ZTpHRVQ6MjAwIiwicnVsZTp3cml0ZS9nZW9kYXRhL190YWc6UE9TVDoyMDAiXSwiaHR0cDovL2FybGFzLmlvL2dyb3VwcyI6WyJUZXN0IEdyb3VwIl0sImh0dHA6Ly9hcmxhcy5pby9yb2xlcyI6WyJyb2xlOmF4eGVzRXhwbG9yZXIiXSwibmlja25hbWUiOiJhbGFpbi5ib2RpZ3VlbCIsIm5hbWUiOiJhbGFpbi5ib2RpZ3VlbEBnbWFpbC5jb20iLCJwaWN0dXJlIjoiaHR0cHM6Ly9zLmdyYXZhdGFyLmNvbS9hdmF0YXIvOTZhOTMwNmM5ODg2OTJjM2M0MmRmZTE3OTI1NTUwZTg_cz00ODAmcj1wZyZkPWh0dHBzJTNBJTJGJTJGY2RuLmF1dGgwLmNvbSUyRmF2YXRhcnMlMkZhbC5wbmciLCJ1cGRhdGVkX2F0IjoiMjAxOS0xMS0yMFQxMjozNTo0NS42MjdaIiwiaXNzIjoiaHR0cHM6Ly90ZXN0YXJsYXMuZXUuYXV0aDAuY29tLyIsInN1YiI6ImF1dGgwfDVkM2VkZjk0YmI0OGI0MGVjNzAxNmY0YSIsImF1ZCI6IkF6Tmdna3hIaFNaVEVwQjRVaEhwNW5QY3RRYlY0Y1VIIiwiaWF0IjoxNTc0MjUzNDEwLCJleHAiOjE4ODk2MTM0MTB9.fJ_nUeyp2az6TZqveMXATH67v9-M_1HuVeah1Ef6l0UNjb6i5H-fVvCaK-d0ps5zy9zF4WwwJ7M72VGlL1sh6T5KqOQFvpVyXm1NAk3RtLwPCGvSc8QH0wIDmEnqGG5kacCVOgdlsZfU7fjAig5632aGvm8nfqfjQD_AnOgc4R5LLDZoKPnTrS5cAnOniMEzGLG8oGJAdCAfn1vfXK6LpEL8-B2Ym0ct0MgzNVaE46rOs3A5FK1uvy-nm7F69F5SQYdAm55vF23mlsUTSfFAyxL9OArWS4tEgMbMD5nNuhPRFHLk_EKKoe7LWfSHqyxkPTXvugODbAvYN_VzBc1hIQ";


    private static final String TAG_SUFFIX = "/_tag";
    private static final String UNTAG_SUFFIX = "/_untag";

    Logger LOGGER = LoggerFactory.getLogger(TagAuthIT.class);

    @Override
    public String getTaggerUrlPath(String collection) {
        super.setTaggerRestAssured();
        LOGGER.info(arlasTaggerPath);
        return arlasTaggerPath + "write/"+collection;
    }

    @Before
    public void before(){
        super.setServerRestAssured();
        ArlasServerContext.beforeClass();
    }

    @After
    public void after() throws IOException {
        super.setServerRestAssured();
        ArlasServerContext.afterClass();
    }

    @Test
    public void testAccessProtectedResourceWithoutAuthHeader() throws Exception {
        super.setTaggerRestAssured();
        given().contentType("application/json")
                .body(getTagRequest())
                .when()
                .post(getTaggerUrlPath(CollectionTool.COLLECTION_NAME)+TAG_SUFFIX)
                .then()
                .statusCode(401);
    }

    @Test
    public void testAccessProtectedResourceWithInvalidAuthHeader() throws Exception {
        super.setTaggerRestAssured();
        given().header(HttpHeaders.AUTHORIZATION, "foo")
                .contentType("application/json")
                .body(getTagRequest())
                .when()
                .post(getTaggerUrlPath(CollectionTool.COLLECTION_NAME)+TAG_SUFFIX)
                .then()
                .statusCode(401);
    }

    @Test
    public void testAccessProtectedResourceWithValidAuthHeader() throws Exception {
        super.setTaggerRestAssured();

        given().header(HttpHeaders.AUTHORIZATION, token)
                .contentType("application/json")
                .body(getTagRequest())
                .when()
                .post(getTaggerUrlPath(CollectionTool.COLLECTION_NAME)+TAG_SUFFIX)
                .then()
                .statusCode(200);
    }

    @Test
    public void testAccessForbiddenResourceWithValidAuthHeader() throws Exception {
        super.setTaggerRestAssured();

        given().contentType("application/json")
                .body(getTagRequest())
                .when()
                .post(getTaggerUrlPath(CollectionTool.COLLECTION_NAME)+UNTAG_SUFFIX)
                .then()
                .statusCode(401);
   }

   private TagRequest getTagRequest() {
       TagRequest tr = new TagRequest();
       tr.tag=new Tag();
       tr.tag.path="params.tags";
       tr.tag.value="v1";
       return tr;
   }
}
