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

import io.arlas.server.exceptions.ArlasException;
import io.arlas.tagger.AbstractTaggerTestContext;
import io.arlas.tagger.ArlasServerContext;
import io.arlas.tagger.CollectionTool;
import io.arlas.tagger.model.Tag;
import io.arlas.tagger.model.request.TagRequest;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.junit.*;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TagIT extends AbstractTaggerTestContext {
    private static final String TAG_SUFFIX = "/_tag";
    private static final String UNTAG_SUFFIX = "/_untag";

    Logger LOGGER = LoggerFactory.getLogger(TagIT.class);

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
    public void after() throws ArlasException {
        super.setServerRestAssured();
        ArlasServerContext.afterClass();
    }

    @Test
    public void test01AddTag() throws InterruptedException {
        super.setTaggerRestAssured();
        TagRequest tr = new TagRequest();
        tr.tag=new Tag();
        tr.tag.path="params.tags";
        tr.tag.value="v1";

        given().contentType("application/json")
                .body(tr)
                .when()
                .post(getTaggerUrlPath(CollectionTool.COLLECTION_NAME)+TAG_SUFFIX)
                .then()
                .statusCode(200);

        Thread.currentThread().sleep(30000);

        super.setServerRestAssured();
        given()
                .get(ArlasServerContext.getArlasServerUrlPath(CollectionTool.COLLECTION_NAME))
                .then()
                .statusCode(200)
                .body("hits[0].data.params.tags.size()",equalTo(1))
                .body("hits.data.params.tags[0]", everyItem(equalTo("v1")))
        ;

        super.setTaggerRestAssured();

        tr.tag.value="v2";
        given().contentType("application/json")
                .body(tr)
                .when()
                .post(getTaggerUrlPath(CollectionTool.COLLECTION_NAME)+TAG_SUFFIX)
                .then()
                .statusCode(200);

        Thread.currentThread().sleep(30000);

        super.setServerRestAssured();
        given()
                .get(ArlasServerContext.getArlasServerUrlPath(CollectionTool.COLLECTION_NAME))
                .then()
                .statusCode(200)
                .body("hits[0].data.params.tags.size()",equalTo(2))
                .body("hits[0].data.params.tags[0]", (equalTo("v1")))
                .body("hits[0].data.params.tags[1]", (equalTo("v2")))
        ;
        // No longer relevant as the tagging is now asynchronous and we cannot get this error in real time
//        Thread.currentThread().sleep(5000);
//        tr.tag.path="params.not.allowed.tags";
//        given().contentType("application/json")
//                .body(tr)
//                .when()
//                .post(getUrlPath(CollectionTool.COLLECTION_NAME)+TAG_SUFFIX)
//                .then()
//                .statusCode(400)
//                .body("error",equalTo("io.arlas.server.exceptions.NotAllowedException"))
//        ;
    }


    @Test
    public void test02AddTagOnSingleValue() throws InterruptedException {
        super.setTaggerRestAssured();
        TagRequest tr = new TagRequest();
        tr.tag=new Tag();
        tr.tag.path="params.job";
        tr.tag.value="Another job";

        given().contentType("application/json")
                .body(tr)
                .when()
                .post(getTaggerUrlPath(CollectionTool.COLLECTION_NAME)+TAG_SUFFIX)
                .then()
                .statusCode(200);

        Thread.currentThread().sleep(30000);

        super.setServerRestAssured();
        given()
                .get(ArlasServerContext.getArlasServerUrlPath(CollectionTool.COLLECTION_NAME))
                .then()
                .statusCode(200)
                .body("hits[0].data.params.job.size()",equalTo(2))
                .body("hits[0].data.params.job[1]", equalTo("Another job"))
        ;
    }


    @Test
    public void test03Untag() throws InterruptedException {
        super.setTaggerRestAssured();
        TagRequest tr = new TagRequest();
        tr.tag = new Tag();
        tr.tag.path = "params.tags";
        tr.tag.value = "v1";

        // TAG v1
        given().contentType("application/json")
                .body(tr)
                .when()
                .post(getTaggerUrlPath(CollectionTool.COLLECTION_NAME)+TAG_SUFFIX)
                .then()
                .statusCode(200);

        Thread.currentThread().sleep(32000);

        // TAG v2
        tr.tag.value = "v2";
        given().contentType("application/json")
                .body(tr)
                .when()
                .post(getTaggerUrlPath(CollectionTool.COLLECTION_NAME)+TAG_SUFFIX)
                .then()
                .statusCode(200);

        Thread.currentThread().sleep(32000);

        // UNTAG v1
        tr.tag.value = "v1";
        given().contentType("application/json")
                .body(tr)
                .when()
                .post(getTaggerUrlPath(CollectionTool.COLLECTION_NAME)+UNTAG_SUFFIX)
                .then()
                .statusCode(200);

        Thread.currentThread().sleep(35000);

        super.setServerRestAssured();
        // Only v2 remains
        given()
                .get(ArlasServerContext.getArlasServerUrlPath(CollectionTool.COLLECTION_NAME))
                .then()
                .statusCode(200)
                .body("hits[0].data.params.tags.size()", equalTo(1))
                .body("hits.data.params.tags[0]", everyItem(equalTo("v2")))
        ;

        super.setTaggerRestAssured();
        // TAG v3
        tr.tag.value = "v3";
        given().contentType("application/json")
                .body(tr)
                .when()
                .post(getTaggerUrlPath(CollectionTool.COLLECTION_NAME)+TAG_SUFFIX)
                .then()
                .statusCode(200);
        Thread.currentThread().sleep(30000);

        // UNTAG all
        TagRequest remove = new TagRequest();
        remove.tag = new Tag();
        remove.tag.path = "params.tags";
        given().contentType("application/json")
                .body(remove)
                .when()
                .post(getTaggerUrlPath(CollectionTool.COLLECTION_NAME)+UNTAG_SUFFIX)
                .then()
                .statusCode(200);

        boolean success = doUntil(o -> {
                    super.setServerRestAssured();
                    return given()
                            .get(ArlasServerContext.getArlasServerUrlPath(CollectionTool.COLLECTION_NAME))
                            .then()
                            .extract().path("hits.data.params.tags"); }
                , everyItem(nullValue()), 10, 10);
        Assert.assertTrue("hits.data.params.tags are not null", success);

        // No longer relevant as the tagging is now asynchronous and we cannot get this error in real time
        // Not allowed tag
//        Thread.currentThread().sleep(5000);
//        tr.tag.path="params.not.allowed.tags";
//        given().contentType("application/json")
//                .body(tr)
//                .when()
//                .post(getUrlPath(CollectionTool.COLLECTION_NAME)+UNTAG_SUFFIX)
//                .then()
//                .statusCode(400)
//                .body("error",equalTo("io.arlas.server.exceptions.NotAllowedException"))
//        ;
    }

    public <R> boolean doUntil(Function<R, Object> function, Matcher matcher, int tries, int waitseconds) throws InterruptedException {
        for (int i = 0; i < tries; i++) {
            Thread.sleep(waitseconds * 1000);
            Object o = function.apply(null);
            if (matcher.matches(o)) {
                return true;
            }
            Description description = new StringDescription();
            matcher.describeMismatch(o, description);
            LOGGER.error("Assertion failed :" + description);
        }
        LOGGER.error("Assertion failed " + tries + " times, we give up.");
        return false;
    }
}