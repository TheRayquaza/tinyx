package com.epita.repo_social.controller;

import com.epita.exchange.utils.Logger;
import com.epita.repo_social.RepoSocialErrorCode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("/social")
public interface RepoSocialControllerApi extends Logger {
  @POST
  @Path("/post/{postId}/like")
  @Operation(summary = "Like a post")
  @APIResponses({
    @APIResponse(responseCode = "201", description = "Like created"),
    @APIResponse(
        responseCode = "400",
        description = "Invalid input",
        content = @Content(schema = @Schema(implementation = RepoSocialErrorCode.class))),
    @APIResponse(
        responseCode = "401",
        description = "User not authorized",
        content = @Content(schema = @Schema(implementation = RepoSocialErrorCode.class))),
    @APIResponse(
        responseCode = "403",
        description = "User blocked",
        content = @Content(schema = @Schema(implementation = RepoSocialErrorCode.class))),
    @APIResponse(
        responseCode = "404",
        description = "Error during the cypher script execution",
        content = @Content(schema = @Schema(implementation = RepoSocialErrorCode.class))),
  })
  @NotNull
  void likePost(@PathParam("postId") @NotNull @Valid String postId);

  @DELETE
  @Path("/post/{postId}/like")
  @Operation(summary = "Deletes the like of a post")
  @APIResponses({
    @APIResponse(responseCode = "201", description = "Like created"),
    @APIResponse(
        responseCode = "400",
        description = "Invalid input",
        content = @Content(schema = @Schema(implementation = RepoSocialErrorCode.class))),
    @APIResponse(
        responseCode = "401",
        description = "User not authorized",
        content = @Content(schema = @Schema(implementation = RepoSocialErrorCode.class))),
    @APIResponse(
        responseCode = "403",
        description = "User blocked",
        content = @Content(schema = @Schema(implementation = RepoSocialErrorCode.class))),
    @APIResponse(
        responseCode = "404",
        description = "Error during the cypher script execution",
        content = @Content(schema = @Schema(implementation = RepoSocialErrorCode.class))),
  })
  @NotNull
  void unlikePost(@PathParam("postId") @NotNull @Valid String postId);

  @POST
  @Path("/user/{userId}/follow")
  @Operation(summary = "follows another user")
  @APIResponses({
    @APIResponse(responseCode = "201", description = "Like created"),
    @APIResponse(
        responseCode = "400",
        description = "Invalid input",
        content = @Content(schema = @Schema(implementation = RepoSocialErrorCode.class))),
    @APIResponse(
        responseCode = "401",
        description = "User not authorized",
        content = @Content(schema = @Schema(implementation = RepoSocialErrorCode.class))),
    @APIResponse(
        responseCode = "403",
        description = "User blocked",
        content = @Content(schema = @Schema(implementation = RepoSocialErrorCode.class))),
    @APIResponse(
        responseCode = "404",
        description = "Error during the cypher script execution",
        content = @Content(schema = @Schema(implementation = RepoSocialErrorCode.class))),
  })
  @NotNull
  void followUser(@PathParam("userId") @NotNull @Valid String userId);

  @DELETE
  @Path("/user/{userId}/follow")
  @Operation(summary = "unfollows another user")
  @APIResponses({
    @APIResponse(responseCode = "201", description = "Like created"),
    @APIResponse(
        responseCode = "400",
        description = "Invalid input",
        content = @Content(schema = @Schema(implementation = RepoSocialErrorCode.class))),
    @APIResponse(
        responseCode = "401",
        description = "User not authorized",
        content = @Content(schema = @Schema(implementation = RepoSocialErrorCode.class))),
    @APIResponse(
        responseCode = "403",
        description = "User blocked",
        content = @Content(schema = @Schema(implementation = RepoSocialErrorCode.class))),
    @APIResponse(
        responseCode = "404",
        description = "Error during the cypher script execution",
        content = @Content(schema = @Schema(implementation = RepoSocialErrorCode.class))),
  })
  @NotNull
  void unfollowUser(@PathParam("userId") @NotNull @Valid String userId);

  @POST
  @Path("/user/{userId}/block")
  @Operation(summary = "blocks another user")
  @APIResponses({
    @APIResponse(responseCode = "201", description = "Like created"),
    @APIResponse(
        responseCode = "400",
        description = "Invalid input",
        content = @Content(schema = @Schema(implementation = RepoSocialErrorCode.class))),
    @APIResponse(
        responseCode = "401",
        description = "User not authorized",
        content = @Content(schema = @Schema(implementation = RepoSocialErrorCode.class))),
    @APIResponse(
        responseCode = "403",
        description = "User blocked",
        content = @Content(schema = @Schema(implementation = RepoSocialErrorCode.class))),
    @APIResponse(
        responseCode = "404",
        description = "Error during the cypher script execution",
        content = @Content(schema = @Schema(implementation = RepoSocialErrorCode.class))),
  })
  @NotNull
  void blockUser(@PathParam("userId") @NotNull @Valid String userId);

  @DELETE
  @Path("/user/{userId}/block")
  @Operation(summary = "unblocks another user")
  @APIResponses({
    @APIResponse(responseCode = "201", description = "Like created"),
    @APIResponse(
        responseCode = "400",
        description = "Invalid input",
        content = @Content(schema = @Schema(implementation = RepoSocialErrorCode.class))),
    @APIResponse(
        responseCode = "401",
        description = "User not authorized",
        content = @Content(schema = @Schema(implementation = RepoSocialErrorCode.class))),
    @APIResponse(
        responseCode = "403",
        description = "User blocked",
        content = @Content(schema = @Schema(implementation = RepoSocialErrorCode.class))),
    @APIResponse(
        responseCode = "404",
        description = "Error during the cypher script execution",
        content = @Content(schema = @Schema(implementation = RepoSocialErrorCode.class))),
  })
  @NotNull
  void unblockUser(@PathParam("userId") @NotNull @Valid String userId);
}
