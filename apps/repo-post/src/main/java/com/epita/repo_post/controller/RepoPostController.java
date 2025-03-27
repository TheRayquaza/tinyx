package com.epita.repo_post.controller;

import com.epita.exchange.auth.service.AuthService;
import com.epita.repo_post.controller.request.CreatePostRequest;
import com.epita.repo_post.controller.request.DeletePostRequest;
import com.epita.repo_post.controller.request.EditPostRequest;
import com.epita.repo_post.controller.request.GetAllRepliesRequest;
import com.epita.repo_post.controller.request.GetPostRequest;
import com.epita.repo_post.controller.request.PostReplyRequest;
import com.epita.repo_post.controller.response.AllRepliesResponse;
import com.epita.repo_post.service.entity.PostEntity;
import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import jakarta.ws.rs.core.MediaType;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/")
public class RepoPostController implements RepoPostControllerApi {

  @Inject AuthService authService;

  @POST
  @Path("/create")
  @Authenticated
  @Override
  public PostEntity createPost(@RequestBody(required = true) @NotNull @Valid CreatePostRequest request) 
  {
    String ownerId = authService.getUserId();
    logger().info("POST /create - User {} creates a post", ownerId);
    return null;
  }

  @GET
  @Path("/post/{id}")
  @Override
  public PostEntity getPostById(@PathParam("id") String id)
  {
    logger().info("GET /post/{} - Retrieve post n°{}", id, id);
    return null;
  }

  @PUT
  @Authenticated
  @Path("/post/{id}")
  @Override
  public Response editPost(@RequestBody(required = true) @NotNull @Valid EditPostRequest request, @PathParam("id") String postId)
  {
    String userId = authService.getUserId();
    logger().info("PUT /post/{} - Edit post {} with user {}", postId, postId, userId);
    return Response.ok().build();
  }

  @DELETE
  @Authenticated
  @Path("/post/{id}")
  @Override
  public Response deletePost(@PathParam("id") String postId)
  {
    String userId = authService.getUserId();
    logger().info("DELETE /post/{} - Delete post with user {}", postId, userId);
    // User can delete its own post only
    return Response.status(204).build();
  }


  @POST
  @Authenticated
  @Override
  @Path("/post/{id}/reply")
  public Response replyToPost(@RequestBody(required = true) @NotNull @Valid PostReplyRequest request, @PathParam("id") String postId)
  {
    String userId = authService.getUserId();
    logger().info("POST /post/{}/reply - Reply to post {} with user {}", postId, userId);
    return Response.status(201).build();
  }

  @GET
  @Authenticated
  @Path("/post/{id}/reply")
  @Override
  public AllRepliesResponse getAllRepliesForPost(@PathParam("id") String postId)
  {
    String userId = authService.getUserId();
    logger().info("Get /post/{}/reply - Get all replies from post {}", postId, postId);
    return null;
  }

}
