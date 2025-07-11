package com.epita.repo_post.controller;

import com.epita.exchange.utils.Logger;
import com.epita.repo_post.controller.request.CreatePostRequest;
import com.epita.repo_post.controller.request.EditPostRequest;
import com.epita.repo_post.controller.request.PostReplyRequest;
import com.epita.repo_post.controller.response.AllRepliesResponse;
import com.epita.repo_post.service.entity.PostEntity;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("/post")
@Tag(name = "RepoPost", description = "RepoPost")
public interface RepoPostControllerApi extends Logger {

  @POST
  @Path("/")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Operation(summary = "Create a new post")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Post created successfully",
        content = @Content(schema = @Schema(implementation = PostEntity.class))),
    @APIResponse(responseCode = "400", description = "Invalid input"),
    @APIResponse(responseCode = "401", description = "User not authorized"),
    @APIResponse(responseCode = "404", description = "User Not Found"),
  })
  @NotNull
  PostEntity createPost(@RequestBody(required = true) @NotNull @Valid CreatePostRequest request);

  @GET
  @Path("/{id}")
  @Operation(summary = "Get a specific post")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Post found",
        content = @Content(schema = @Schema(implementation = PostEntity.class))),
    @APIResponse(responseCode = "400", description = "Invalid input"),
    // @APIResponse(responseCode = "401", description = "User not authorized"),
    @APIResponse(responseCode = "404", description = "Post Not Found"),
  })
  @NotNull
  PostEntity getPostById(@PathParam("id") String id);

  @PUT
  @Path("/{id}")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Operation(summary = "Edit a post")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Post edited successfully",
        content = @Content(schema = @Schema(implementation = PostEntity.class))),
    @APIResponse(responseCode = "400", description = "Invalid input"),
    @APIResponse(responseCode = "401", description = "User not authorized to modify Post"),
    @APIResponse(responseCode = "404", description = "Post or User Not Found"),
  })
  @NotNull
  PostEntity editPost(
      @RequestBody(required = true) @NotNull @Valid EditPostRequest request,
      @PathParam("id") String id);

  @DELETE
  @Path("/{id}")
  @Operation(summary = "Delete a post")
  @APIResponses({
    @APIResponse(
        responseCode = "204",
        description = "Post deleted successfully",
        content = @Content(schema = @Schema(implementation = PostEntity.class))),
    @APIResponse(responseCode = "400", description = "Invalid input"),
    @APIResponse(responseCode = "401", description = "User not authorized to delete"),
    @APIResponse(responseCode = "404", description = "User Not Found"),
  })
  @NotNull
  void deletePost(@PathParam("id") @Valid String id);

  @POST
  @Path("/{id}/reply")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Operation(summary = "Reply to a post")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Post created successfully",
        content = @Content(schema = @Schema(implementation = PostEntity.class))),
    @APIResponse(responseCode = "400", description = "Invalid input"),
    @APIResponse(responseCode = "401", description = "User not authorized"),
    @APIResponse(responseCode = "404", description = "User Not Found"),
  })
  @NotNull
  PostEntity replyToPost(
      @RequestBody(required = true) @NotNull @Valid PostReplyRequest request,
      @PathParam("id") String id);

  @GET
  @Path("/{id}/reply")
  @Operation(summary = "Get all replies for a post")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Post created successfully",
        content = @Content(schema = @Schema(implementation = PostEntity.class))),
    @APIResponse(responseCode = "400", description = "Invalid input"),
    @APIResponse(responseCode = "401", description = "User not authorized"),
    @APIResponse(responseCode = "404", description = "User Not Found"),
  })
  @NotNull
  AllRepliesResponse getAllRepliesForPost(@PathParam("id") String id);
}
