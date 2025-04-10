package com.epita.srvc_media.controller;

import com.epita.exchange.utils.Logger;
import com.epita.srvc_media.SrvcMediaErrorCode;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

@Produces(MediaType.APPLICATION_OCTET_STREAM)
@Path("/minio")
public interface SrvcMediaControllerApi extends Logger {
  @GET
  @Path("/{id}")
  @Operation(summary = "Get media object")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Media Object",
        content = @Content(schema = @Schema(implementation = Response.class))),
    @APIResponse(
        responseCode = "400",
        description = "Invalid input",
        content = @Content(schema = @Schema(implementation = SrvcMediaErrorCode.class))),
    @APIResponse(
        responseCode = "404",
        description = "Object Not Found",
        content = @Content(schema = @Schema(implementation = SrvcMediaErrorCode.class))),
  })
  @NotNull
  Response downloadObject(@PathParam("id") @NotNull String id);
}
