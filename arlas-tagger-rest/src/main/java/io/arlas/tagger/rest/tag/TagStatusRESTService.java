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
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/status")
@Tag(name="status", description="Tagger status API")
@OpenAPIDefinition(
        info = @Info(
                title = "ARLAS Tagger APIs",
                description = "(Un)Tag fields of ARLAS collections",
                license = @License(name = "Apache 2.0", url = "https://www.apache.org/licenses/LICENSE-2.0.html"),
                contact = @Contact(email = "contact@gisaia.com", name = "Gisaia", url = "http://www.gisaia.com/"),
                version = "API_VERSION"),
        externalDocs = @ExternalDocumentation(
                description = "API documentation",
                url="https://docs.arlas.io/arlas-api/"),
        servers = {
                @Server(url = "/arlas_tagger", description = "default server")
        }
)

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
    @Operation(
            summary = "TagStatus",
            description = Documentation.TAGSTATUS_OPERATION
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(implementation = UpdateResponse.class))),
            @ApiResponse(responseCode = "500", description = "Arlas Server Error.",
                    content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "400", description = "Bad request.",
                    content = @Content(schema = @Schema(implementation = Error.class)))
    })
    public Response taggingGet(
            // --------------------------------------------------------
            // ----------------------- PATH     -----------------------
            // --------------------------------------------------------
            @Parameter(
                    name = "collection",
                    description = "collection",
                    required = true)
            @PathParam(value = "collection") String collection,

            @Parameter(name = "id", description = Documentation.TAGSTATUS_PARAM_ID,
                    required = true)
            @PathParam(value = "id") String id,
            // --------------------------------------------------------
            // ----------------------- FORM     -----------------------
            // --------------------------------------------------------
            @Parameter(name ="pretty",
                    description=Documentation.FORM_PRETTY,
                    schema = @Schema(defaultValue = "false"))
            @QueryParam(value="pretty") Boolean pretty
    ) {
        return Response.ok(status.getStatus(id).orElseGet(UpdateResponse::new)).build();
    }

    @Timed
    @Path("/{collection}/_taglist")
    @GET
    @Produces(UTF8JSON)
    @Consumes(UTF8JSON)
    @Operation(
            summary = "TagList",
            description = Documentation.TAGLIST_OPERATION
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TagRefRequest.class)))),
            @ApiResponse(responseCode = "500", description = "Arlas Server Error.",
                    content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "400", description = "Bad request.",
                    content = @Content(schema = @Schema(implementation = Error.class)))
    })
    public Response taggingGetList(
            // --------------------------------------------------------
            // ----------------------- PATH     -----------------------
            // --------------------------------------------------------
            @Parameter(
                    name = "collection",
                    description = "collection",
                    required = true)
            @PathParam(value = "collection") String collection,

            // --------------------------------------------------------
            // ----------------------- FORM     -----------------------
            // --------------------------------------------------------
            @Parameter(name ="pretty", description=Documentation.FORM_PRETTY,
                    schema = @Schema(defaultValue = "false"))
            @QueryParam(value="pretty") Boolean pretty
    ) {

        return Response.ok(tagExploreService.getTagRefList()).build();
    }
}
