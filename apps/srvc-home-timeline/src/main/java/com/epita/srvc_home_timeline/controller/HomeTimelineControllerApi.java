package com.epita.srvc_home_timeline.controller;

import com.epita.exchange.utils.Logger;
import com.epita.srvc_home_timeline.controller.response.HomeTimelineResponse;
import com.epita.srvc_home_timeline.service.entity.HomeTimelineEntity;

import io.quarkus.security.Authenticated;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

@Path("/home-timeline")
public interface HomeTimelineControllerApi extends Logger {
  @GET
  @Path("/{id}")
  @Authenticated
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(summary = "extract the home feed")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Get a list of posts related to a specific user's follows",
        content = @Content(schema = @Schema(implementation = HomeTimelineEntity.class))),
    @APIResponse(responseCode = "400", description = "Invalid Input"),
    @APIResponse(responseCode = "401", description = "User not authorized"),
    @APIResponse(responseCode = "404", description = "User not Found"),
  })
  @NotNull
  HomeTimelineResponse getHomeTimelineById(@PathParam("id") String id);
}
