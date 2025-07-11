package com.epita.repo_post.controller;

import com.epita.exchange.auth.service.AuthService;
import com.epita.repo_post.controller.request.CreatePostRequest;
import com.epita.repo_post.controller.request.EditPostRequest;
import com.epita.repo_post.controller.request.PostReplyRequest;
import com.epita.repo_post.controller.response.AllRepliesResponse;
import com.epita.repo_post.service.PostService;
import com.epita.repo_post.service.entity.PostEntity;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/post")
public class RepoPostController implements RepoPostControllerApi {

  @Inject AuthService authService;

  @Inject PostService postService;

  @POST
  @Path("/")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Override
  public PostEntity createPost(
      @RequestBody(required = true) @NotNull @Valid CreatePostRequest request) {
    String ownerId = authService.getUserId();
    logger().info("POST /create - User {} creates a post", ownerId);
    return postService.createPost(request, ownerId);
  }

  @GET
  @Path("/{id}")
  @Override
  public PostEntity getPostById(@PathParam("id") @Valid String id) {
    logger().info("GET /post/{} - Retrieve post n°{}", id, id);
    return postService.getPostById(id);
  }

  @PUT
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Path("/{id}")
  @Override
  public PostEntity editPost(
      @RequestBody(required = true) @NotNull @Valid EditPostRequest request,
      @PathParam("id") String postId) {
    String userId = authService.getUserId();
    logger().info("PUT /post/{} - Edit post {} with user {}", postId, postId, userId);
    return postService.editPost(request, postId);
  }

  @DELETE
  @Path("/{id}")
  @Override
  public void deletePost(@PathParam("id") String postId) {
    String userId = authService.getUserId();
    logger().info("DELETE /post/{} - Delete post with user {}", postId, userId);
    // User can delete its own post only
    postService.deletePost(postId);
  }

  @POST
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Override
  @Path("/{id}/reply")
  public PostEntity replyToPost(
      @RequestBody(required = true) @NotNull @Valid PostReplyRequest request,
      @PathParam("id") @Valid String postId) {
    String userId = authService.getUserId();
    logger().info("POST /post/{}/reply - Reply to post {} with user {}", postId, postId, userId);
    return postService.replyToPost(request, postId);
  }

  @GET
  @Path("/{id}/reply")
  @Override
  public AllRepliesResponse getAllRepliesForPost(@PathParam("id") String postId) {
    String userId = authService.getUserId();
    logger()
        .info(
            "Get /post/{}/reply - Get all replies from post {} with user {}",
            postId,
            postId,
            userId);
    return postService.getAllRepliesForPost(postId);
  }
}
