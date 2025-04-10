package com.epita.repo_social.controller;

import com.epita.exchange.auth.service.AuthService;
import com.epita.repo_social.controller.response.PostResponse;
import com.epita.repo_social.controller.response.UserResponse;
import com.epita.repo_social.converter.PostEntityToPostResponse;
import com.epita.repo_social.converter.UserEntityToUserResponse;
import com.epita.repo_social.service.SocialService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("/social")
public class RepoSocialController implements RepoSocialControllerApi {

  @Inject AuthService authService;

  @Inject SocialService socialService;

  @Inject UserEntityToUserResponse userEntityToUserResponse;
  @Inject PostEntityToPostResponse postEntityToPostResponse;

  @POST
  @Path("/post/{id}/like")
  @Override
  public Response likePost(@PathParam("id") String postId) {
    logger().info("POST /social/post/{}/like", postId);
    socialService.likePost(postId);
    return Response.status(Response.Status.CREATED).build();
  }

  @DELETE
  @Path("/post/{id}/like")
  @Override
  public Response unlikePost(@PathParam("id") String postId) {
    logger().info("DELETE /social/post/{}/like", postId);
    socialService.unlikePost(postId);
    return Response.status(Response.Status.NO_CONTENT).build();
  }

  @POST
  @Path("/user/{id}/follow")
  @Override
  public Response followUser(@PathParam("id") String userId) {
    logger().info("POST /social/user/{}/follow", userId);
    socialService.followUser(userId);
    return Response.status(Response.Status.CREATED).build();
  }

  @DELETE
  @Path("/user/{id}/follow")
  @Override
  public Response unfollowUser(@PathParam("id") String userId) {
    logger().info("DELETE /social/user/{}/follow", userId);
    socialService.unfollowUser(userId);
    return Response.status(Response.Status.NO_CONTENT).build();
  }

  @POST
  @Path("/user/{id}/block")
  @Override
  public Response blockUser(@PathParam("id") String userId) {
    logger().info("POST /social/user/{}/block", userId);
    socialService.blockUser(userId);
    return Response.status(Response.Status.CREATED).build();
  }

  @DELETE
  @Path("/user/{id}/block")
  @Override
  public Response unblockUser(@PathParam("id") String userId) {
    logger().info("DELETE /social/user/{}/block", userId);
    socialService.unblockUser(userId);
    return Response.status(Response.Status.NO_CONTENT).build();
  }

  @GET
  @Path("/post/{id}/like")
  public List<UserResponse> getLikes(@PathParam("id") String postId) {
    logger().info("GET /social/post/{}/like", postId);
    return socialService.getPostLikes(postId).stream()
        .map(userEntityToUserResponse::convertNotNull)
        .toList();
  }

  @GET
  @Path("/user/{id}/like")
  public List<PostResponse> getUserLikedPosts(@PathParam("id") String userId) {
    logger().info("GET /social/user/{}/like", userId);
    return socialService.getUserLikedPosts(userId).stream()
        .map(postEntityToPostResponse::convertNotNull)
        .toList();
  }

  @GET
  @Path("/user/{id}/follower")
  public List<UserResponse> getFollowers(@PathParam("id") String userId) {
    logger().info("GET /social/user/{}/follower", userId);
    return socialService.getUserFollowers(userId).stream()
        .map(userEntityToUserResponse::convertNotNull)
        .toList();
  }

  @GET
  @Path("/user/{id}/following")
  public List<UserResponse> getFollowings(@PathParam("id") String userId) {
    logger().info("GET /social/user/{}/following", userId);
    return socialService.getUserFollowings(userId).stream()
        .map(userEntityToUserResponse::convertNotNull)
        .toList();
  }

  @GET
  @Path("/user/{id}/blocked")
  public List<UserResponse> getBlocked(@PathParam("id") String userId) {
    logger().info("GET /social/user/block");
    return socialService.getUserBlocked(userId).stream()
        .map(userEntityToUserResponse::convertNotNull)
        .toList();
  }

  @GET
  @Path("/user/{id}/blockedBy")
  public List<UserResponse> getBlockedBy(@PathParam("id") String userId) {
    logger().info("GET /social/user/block");
    return socialService.getUserBlockedBy(userId).stream()
        .map(userEntityToUserResponse::convertNotNull)
        .toList();
  }
}
