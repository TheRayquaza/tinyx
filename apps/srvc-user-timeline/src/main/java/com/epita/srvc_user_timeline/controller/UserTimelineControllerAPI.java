package com.epita.srvc_user_timeline.controller;

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
                    content = @Content(schema = @Schema(implementation = UserEntity.class))),
            @APIResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(schema = @Schema(implementation = UserTimelineErrorCode.class)))
    })
    UserEntity getUserAccount(@PathParam("id") String id);
}
