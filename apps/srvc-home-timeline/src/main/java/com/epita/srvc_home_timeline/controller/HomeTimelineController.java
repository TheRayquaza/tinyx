package com.epita.srvc_home_timeline.controller;

import com.epita.exchange.auth.service.AuthService;
import com.epita.srvc_home_timeline.HomeTimelineErrorCode;
import com.epita.srvc_home_timeline.controller.response.HomeTimelineResponse;
import com.epita.srvc_home_timeline.service.HomeTimelineService;
import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/home-timeline")
public class HomeTimelineController implements HomeTimelineControllerApi {
  @Inject HomeTimelineService homeTimelineService;
  @Inject AuthService authService;

  @GET
  @Path("/{id}")
  @Authenticated
  @Override
  public HomeTimelineResponse getHomeTimelineById(@PathParam("id") @Valid String id) {
    logger().info("GET /home-timeline/{} - Retrieve Home Timeline for user {}", id, id);

    // if (authService.getUserId() != id) {
    //   throw HomeTimelineErrorCode.UNAUTHORIZED.createError(id);
    // }

    return homeTimelineService.getHomeTimelineById(id);
  }
}
