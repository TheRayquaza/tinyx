package com.epita.repo_social.controller;

import com.epita.exchange.auth.service.AuthService;
import com.epita.repo_social.controller.response.UserResponse;
import com.epita.repo_social.converter.UserEntityToUserResponse;
import com.epita.repo_social.service.SocialService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("/social")
public class RepoSocialController implements RepoSocialControllerApi {

  @Inject AuthService authService;

  @Inject SocialService socialService;

  @Inject
  UserEntityToUserResponse userEntityToUserResponse;

  @POST
  @Path("/post/{id}/like")
  @Override
  public void likePost(@PathParam("id") String postId) {
    logger().info("POST /social/post/{}/like", postId);
    socialService.likePost(postId);
  }

  @DELETE
  @Path("/post/{id}/like")
  @Override
  public void unlikePost(@PathParam("id") String postId) {
    logger().info("DELETE /social/post/{}/like", postId);
    socialService.unlikePost(postId);
  }

  @POST
  @Path("/user/{id}/follow")
  @Override
  public void followUser(@PathParam("id") String userId) {
    logger().info("POST /social/user/{}/follow", userId);
    socialService.followUser(userId);
  }

  @DELETE
  @Path("/user/{id}/follow")
  @Override
  public void unfollowUser(@PathParam("id") String userId) {
    logger().info("DELETE /social/user/{}/follow", userId);
    socialService.unfollowUser(userId);
  }

  @POST
  @Path("/user/{id}/block")
  @Override
  public void blockUser(@PathParam("id") String userId) {
    logger().info("POST /social/user/{}/block", userId);
    socialService.blockUser(userId);
  }

  @DELETE
  @Path("/user/{id}/block")
  @Override
  public void unblockUser(@PathParam("id") String userId) {
    logger().info("DELETE /social/user/{}/block", userId);
    socialService.unblockUser(userId);
  }

  @GET
  @Path("/post/{id}/like")
  public List<UserResponse> getUsersLike (@PathParam("id") String postId) {
    logger().info("GET /social/post/{}/like", postId);
    return socialService.getPostLikes(postId).stream()
        .map(userEntityToUserResponse::convertNotNull)
        .toList();
  }

  @GET
  @Path("/user/{id}/follower")
  public List<UserResponse> getFollowers (@PathParam("id") String userId) {
      logger().info("GET /social/user/{}/follower", userId);
      return socialService.getUserFollowers(userId).stream()
          .map(userEntityToUserResponse::convertNotNull)
          .toList();
  }

  @GET
  @Path("/user/{id}/following")
  public List<UserResponse> getFollowings (@PathParam("id") String userId) {
      logger().info("GET /social/user/{}/following", userId);
      return socialService.getUserFollowings(userId).stream()
          .map(userEntityToUserResponse::convertNotNull)
          .toList();
  }

    @GET
    @Path("/user/{id}/block")
    public List<UserResponse> getBlockedUsers (@PathParam("id") String userId) {
        logger().info("GET /social/user/{}/block", userId);
        return socialService.getUserBlocks(userId).stream()
            .map(userEntityToUserResponse::convertNotNull)
            .toList();
    }
}
