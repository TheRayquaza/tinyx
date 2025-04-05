package com.epita.srvc_home_timeline.controller;

import com.epita.exchange.auth.service.AuthService;
import com.epita.srvc_home_timeline.controller.response.HomeTimelineResponse;
import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/home-timeline")
public class SrvcHomeTimelineController implements SrvcHomeTimelineControllerApi {
    @Inject AuthService authService;

    @GET
    @Path("/{id}")
    @Authenticated
    @Override
    public HomeTimelineResponse getHomeTimelineById(@PathParam("id") @Valid String id) {
        logger().info("GET /home-timeline/{} - Retrieve Home Timeline for user {}", id, id);
        return new HomeTimelineResponse();
    }
}
