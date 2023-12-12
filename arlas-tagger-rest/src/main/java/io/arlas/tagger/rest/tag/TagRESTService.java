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
import io.arlas.commons.exceptions.ArlasException;
import io.arlas.commons.rest.response.Error;
import io.arlas.server.core.model.CollectionReference;
import io.arlas.server.core.utils.ColumnFilterUtil;
import io.arlas.tagger.app.Documentation;
import io.arlas.tagger.model.TaggingStatus;
import io.arlas.tagger.model.enumerations.Action;
import io.arlas.tagger.model.request.TagRefRequest;
import io.arlas.tagger.model.request.TagRequest;
import io.arlas.tagger.model.response.UpdateResponse;
import io.arlas.tagger.service.ManagedKafkaConsumers;
import io.arlas.tagger.util.TagRequestFieldsExtractor;
import io.swagger.annotations.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;

import static io.arlas.commons.rest.utils.ServerConstants.*;

@Path("/write")
@Api(value = "/write")
@SwaggerDefinition(
        info = @Info(contact = @Contact(email = "contact@gisaia.com", name = "Gisaia", url = "http://www.gisaia.com/"),
                title = "ARLAS Tagger API",
                description = "(Un)Tag fields of ARLAS collections",
                license = @License(name = "Apache 2.0", url = "https://www.apache.org/licenses/LICENSE-2.0.html"),
                version = "24.1.0"),
        schemes = { SwaggerDefinition.Scheme.HTTP, SwaggerDefinition.Scheme.HTTPS })

public class TagRESTService {
    public static final String UTF8JSON = MediaType.APPLICATION_JSON + ";charset=utf-8";

    private final ManagedKafkaConsumers consumersManager;
    private final Long statusTimeout;

    public TagRESTService(ManagedKafkaConsumers consumersManager, Long statusTimeout) {
        this.consumersManager = consumersManager;
        this.statusTimeout = statusTimeout;
    }

    @Timed
    @Path("/{collection}/_tag")
    @POST
    @Produces(UTF8JSON)
    @Consumes(UTF8JSON)
    @ApiOperation(value = "Tag", produces = UTF8JSON, notes = Documentation.TAG_OPERATION, consumes = UTF8JSON, response = UpdateResponse.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Successful operation", response = UpdateResponse.class),
            @ApiResponse(code = 500, message = "Arlas Server Error.", response = Error.class), @ApiResponse(code = 400, message = "Bad request.", response = Error.class) })
    public Response tagPost(
            // --------------------------------------------------------
            // ----------------------- PATH     -----------------------
            // --------------------------------------------------------
            @ApiParam(
                    name = "collection",
                    value = "collection",
                    required = true)
            @PathParam(value = "collection") String collection,
            // --------------------------------------------------------
            // ----------------------- SEARCH   -----------------------
            // --------------------------------------------------------
            TagRequest tagRequest,

            // --------------------------------------------------------
            // -----------------------  FILTER  -----------------------
            // --------------------------------------------------------

            @ApiParam(hidden = true)
            @HeaderParam(value= PARTITION_FILTER) String partitionFilter,

            @ApiParam(hidden = true)
            @HeaderParam(value = COLUMN_FILTER) String columnFilter,

            @ApiParam(hidden = true)
            @HeaderParam(value = ARLAS_ORGANISATION) String organisations,

            // --------------------------------------------------------
            // ----------------------- FORM     -----------------------
            // --------------------------------------------------------
            @ApiParam(name ="pretty", value=Documentation.FORM_PRETTY,
                    defaultValue = "false")
            @QueryParam(value="pretty") Boolean pretty
    ) throws ArlasException {
        assertColumnFilter(collection, tagRequest, Optional.ofNullable(columnFilter), Optional.ofNullable(organisations));
        if (tagRequest.tag != null && tagRequest.tag.path != null && tagRequest.tag.value != null) {
            TagRefRequest tagRefRequest = TagRefRequest.fromTagRequest(tagRequest, collection, partitionFilter, Action.ADD);
            return doAction(tagRefRequest);
        } else {
            throw new BadRequestException("Tag element is missing required data.");
        }
    }

    @Timed
    @Path("/{collection}/_tagreplay")
    @POST
    @Produces(UTF8JSON)
    @Consumes(UTF8JSON)
    @ApiOperation(value = "TagReplay", produces = UTF8JSON, notes = Documentation.TAG_REPLAY, consumes = UTF8JSON, response = Long.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Successful operation", response = Long.class),
            @ApiResponse(code = 500, message = "Arlas Server Error.", response = Error.class), @ApiResponse(code = 400, message = "Bad request.", response = Error.class) })
    public Response tagReplay(
            // --------------------------------------------------------
            // ----------------------- PATH     -----------------------
            // --------------------------------------------------------
            @ApiParam(
                    name = "collection",
                    value = "collection",
                    required = true)
            @PathParam(value = "collection") String collection,
            // --------------------------------------------------------
            // ----------------------- SEARCH   -----------------------
            // --------------------------------------------------------
            @ApiParam(name = "offset", value = Documentation.TAG_REPLAY_PARAM_OFFSET,
                    required = true)
            @QueryParam(value = "offset") Long offset,

            // --------------------------------------------------------
            // ----------------------- FORM     -----------------------
            // --------------------------------------------------------
            @ApiParam(name ="pretty", value=Documentation.FORM_PRETTY,
                    defaultValue = "false")
            @QueryParam(value="pretty") Boolean pretty
    ) {

        consumersManager.replayFrom(offset);
        return Response.ok(offset).build();
    }

    @Timed
    @Path("/{collection}/_untag")
    @POST
    @Produces(UTF8JSON)
    @Consumes(UTF8JSON)
    @ApiOperation(value = "Untag", produces = UTF8JSON, notes = Documentation.UNTAG_OPERATION, consumes = UTF8JSON, response = UpdateResponse.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Successful operation", response = UpdateResponse.class),
            @ApiResponse(code = 500, message = "Arlas Server Error.", response = Error.class), @ApiResponse(code = 400, message = "Bad request.", response = Error.class) })
    public Response untagPost(
            // --------------------------------------------------------
            // ----------------------- PATH     -----------------------
            // --------------------------------------------------------
            @ApiParam(
                    name = "collection",
                    value = "collection",
                    required = true)
            @PathParam(value = "collection") String collection,
            // --------------------------------------------------------
            // ----------------------- SEARCH   -----------------------
            // --------------------------------------------------------
            TagRequest tagRequest,

            // --------------------------------------------------------
            // -----------------------  FILTER  -----------------------
            // --------------------------------------------------------

            @ApiParam(hidden = true)
            @HeaderParam(value=PARTITION_FILTER) String partitionFilter,

            @ApiParam(hidden = true)
            @HeaderParam(value = COLUMN_FILTER) String columnFilter,

            @ApiParam(hidden = true)
            @HeaderParam(value = ARLAS_ORGANISATION) String organisations,

            // --------------------------------------------------------
            // ----------------------- FORM     -----------------------
            // --------------------------------------------------------
            @ApiParam(name ="pretty", value=Documentation.FORM_PRETTY,
                    defaultValue = "false")
            @QueryParam(value="pretty") Boolean pretty
    ) throws ArlasException {
        assertColumnFilter(collection, tagRequest, Optional.ofNullable(columnFilter), Optional.ofNullable(organisations));

        if (tagRequest.tag != null && tagRequest.tag.path != null) {
            TagRefRequest tagRefRequest = TagRefRequest.fromTagRequest(tagRequest, collection, partitionFilter,
                    tagRequest.tag.value != null ? Action.REMOVE : Action.REMOVEALL);
            return doAction(tagRefRequest);
        } else {
            throw new BadRequestException("Tag element is missing required data.");
        }
    }

    private void assertColumnFilter(String collection,
                                    TagRequest tagRequest,
                                    Optional<String> columnFilter,
                                    Optional<String> organisations) throws ArlasException {
        CollectionReference collectionReference = consumersManager.getUpdateServices().getCollectionReferenceService()
                .getCollectionReference(collection, organisations);
        if (collectionReference == null) {
            throw new NotFoundException(collection);
        }

        ColumnFilterUtil.assertRequestAllowed(columnFilter, collectionReference, tagRequest, new TagRequestFieldsExtractor());
    }

    private Response doAction(TagRefRequest tagRefRequest) {
        consumersManager.getTagKafkaProducer().sendToTagRefLog(tagRefRequest);

        UpdateResponse updateResponse = new UpdateResponse();
        updateResponse.id = tagRefRequest.id;
        updateResponse.label = tagRefRequest.label;
        updateResponse.action = tagRefRequest.action;
        TaggingStatus.getInstance().initStatus(tagRefRequest.id, updateResponse, statusTimeout);

        return Response.ok(updateResponse).build();
    }
}
