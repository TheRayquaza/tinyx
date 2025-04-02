package com.epita.srvc_user_timeline.controller;

import com.epita.exchange.utils.Logger;
import com.epita.srvc_user_timeline.UserTimelineErrorCode;
import com.epita.srvc_user_timeline.controller.contract.UserTimelineResponse;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("/")
public interface UserTimelineControllerAPI extends Logger {
  @Path("/user-timeline/{id}")
  @Operation(summary = "Get a specific user's account timeline")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "User account timeline retrieved successfully",
        content = @Content(schema = @Schema(implementation = UserTimelineResponse.class))),
    @APIResponse(
        responseCode = "404",
        description = "User not found",
        content = @Content(schema = @Schema(implementation = UserTimelineErrorCode.class)))
  })
  UserTimelineResponse getUserTimeline(@PathParam("id") String id);
}
