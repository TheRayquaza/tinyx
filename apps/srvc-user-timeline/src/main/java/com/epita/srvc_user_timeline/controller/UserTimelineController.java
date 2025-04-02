package com.epita.srvc_user_timeline.controller;

import com.epita.srvc_user_timeline.controller.contract.UserTimelineResponse;
import com.epita.srvc_user_timeline.converter.UserTimelineEntityToUserTimelineResponse;
import com.epita.srvc_user_timeline.service.UserTimelineService;
import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("/")
public class UserTimelineController implements UserTimelineControllerAPI {

  @Inject UserTimelineService userTimelineService;
  @Inject UserTimelineEntityToUserTimelineResponse userTimelineEntityToUserTimelineResponse;

  @GET
  @Path("/user-timeline/{id}")
  @Authenticated
  @Override
  public UserTimelineResponse getUserTimeline(@PathParam("id") String id) {
    logger().info("GET /user-timeline/{}", id);
    return userTimelineEntityToUserTimelineResponse.convertNotNull(
        userTimelineService.getUserTimeline(id));
  }
}
