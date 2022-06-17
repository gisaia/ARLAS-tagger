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

import com.codahale.metrics.annotation.Timed;
import io.arlas.commons.rest.response.Error;
import io.arlas.tagger.app.Documentation;
import io.arlas.tagger.model.TaggingStatus;
import io.arlas.tagger.model.request.TagRefRequest;
import io.arlas.tagger.model.response.UpdateResponse;
import io.arlas.tagger.service.TagExploreService;
import io.swagger.annotations.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/status")
@Api(value = "/status")
public class TagStatusRESTService {
    public static final String UTF8JSON = MediaType.APPLICATION_JSON + ";charset=utf-8";
    private final TaggingStatus status;
    private final TagExploreService tagExploreService;

    public TagStatusRESTService(TagExploreService tagExploreService) {
        this.status = TaggingStatus.getInstance();
        this.tagExploreService = tagExploreService;
    }

    @Timed
    @Path("/{collection}/_tag/{id}")
    @GET
    @Produces(UTF8JSON)
    @Consumes(UTF8JSON)
    @ApiOperation(value = "TagStatus", produces = UTF8JSON, notes = Documentation.TAGSTATUS_OPERATION, consumes = UTF8JSON, response = UpdateResponse.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Successful operation", response = UpdateResponse.class),
            @ApiResponse(code = 500, message = "Arlas Server Error.", response = Error.class), @ApiResponse(code = 400, message = "Bad request.", response = Error.class) })
    public Response taggingGet(
            // --------------------------------------------------------
            // ----------------------- PATH     -----------------------
            // --------------------------------------------------------
            @ApiParam(
                    name = "collection",
                    value = "collection",
                    required = true)
            @PathParam(value = "collection") String collection,

            @ApiParam(name = "id", value = Documentation.TAGSTATUS_PARAM_ID,
                    required = true)
            @PathParam(value = "id") String id,
            // --------------------------------------------------------
            // ----------------------- FORM     -----------------------
            // --------------------------------------------------------
            @ApiParam(name ="pretty", value=Documentation.FORM_PRETTY,
                    defaultValue = "false")
            @QueryParam(value="pretty") Boolean pretty
    ) {
        return Response.ok(status.getStatus(id).orElse(new UpdateResponse())).build();
    }

    @Timed
    @Path("/{collection}/_taglist")
    @GET
    @Produces(UTF8JSON)
    @Consumes(UTF8JSON)
    @ApiOperation(value = "TagList", produces = UTF8JSON, notes = Documentation.TAGLIST_OPERATION, consumes = UTF8JSON, response = TagRefRequest.class, responseContainer = "List")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Successful operation", response = TagRefRequest.class, responseContainer = "List"),
            @ApiResponse(code = 500, message = "Arlas Server Error.", response = Error.class), @ApiResponse(code = 400, message = "Bad request.", response = Error.class) })
    public Response taggingGetList(
            // --------------------------------------------------------
            // ----------------------- PATH     -----------------------
            // --------------------------------------------------------
            @ApiParam(
                    name = "collection",
                    value = "collection",
                    required = true)
            @PathParam(value = "collection") String collection,

            // --------------------------------------------------------
            // ----------------------- FORM     -----------------------
            // --------------------------------------------------------
            @ApiParam(name ="pretty", value=Documentation.FORM_PRETTY,
                    defaultValue = "false")
            @QueryParam(value="pretty") Boolean pretty
    ) {

        return Response.ok(tagExploreService.getTagRefList()).build();
    }
}
