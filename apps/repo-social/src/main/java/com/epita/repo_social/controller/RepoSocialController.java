package com.epita.repo_social.controller;

import com.epita.exchange.auth.service.AuthService;
import com.epita.repo_social.service.SocialService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("/social")
public class RepoSocialController implements RepoSocialControllerApi {

  @Inject AuthService authService;

  @Inject SocialService socialService;

  @POST
  @Path("/post/{postId}/like")
  @Override
  public void likePost(@PathParam("postId") String postId) {
    logger().info("POST /social/post/{}/like", postId);
    socialService.likePost(postId);
  }

  @DELETE
  @Path("/post/{postId}/like")
  @Override
  public void unlikePost(@PathParam("postId") String postId) {
    logger().info("DELETE /social/post/{}/like", postId);
    socialService.unlikePost(postId);
  }

  @POST
  @Path("/user/{userId}/follow")
  @Override
  public void followUser(@PathParam("userId") String userId) {
    logger().info("POST /social/user/{}/follow", userId);
    socialService.followUser(userId);
  }

  @DELETE
  @Path("/user/{userId}/follow")
  @Override
  public void unfollowUser(@PathParam("userId") String userId) {
    logger().info("DELETE /social/user/{}/follow", userId);
    socialService.unfollowUser(userId);
  }

  @POST
  @Path("/user/{userId}/block")
  @Override
  public void blockUser(@PathParam("userId") String userId) {
    logger().info("POST /social/user/{}/block", userId);
    socialService.blockUser(userId);
  }

  @DELETE
  @Path("/user/{userId}/block")
  @Override
  public void unblockUser(@PathParam("userId") String userId) {
    logger().info("DELETE /social/user/{}/block", userId);
    socialService.unblockUser(userId);
  }

  /*

  @GET
  @Path("/social/post/{id}/like")
  public ??? getUsersLike (String postId) {
    // This one gets the list of users liking a post
    logger().info("GET /social/post/{}/like", postId);
    socialService.getUsersLike(postId);
  }
  */

}
