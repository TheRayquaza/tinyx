package com.epita.srvc_user_timeline.controller;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("/")
public class UserTimelineController implements UserTimelineController {
    @GET
    @Path("/user-timeline/{id}")
    @Authenticated
    @Override
    public UserEntity getUserTimeline(@PathParam("id") String id) {
        logger().info("GET /user-timeline/{}", id);
        return userService.getUser(new ObjectId(id));
    }
}
