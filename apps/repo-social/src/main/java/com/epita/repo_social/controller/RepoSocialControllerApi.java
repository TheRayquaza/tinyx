package com.epita.repo_social.controller;

import com.epita.exchange.utils.Logger;
import com.epita.repo_social.RepoSocialErrorCode;
import com.epita.repo_social.controller.response.UserResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

import java.util.List;

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
        description = "Post not found",
        content = @Content(schema = @Schema(implementation = RepoSocialErrorCode.class))),
    @APIResponse(
        responseCode = "500",
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

  @GET
  @Path("/post/{id}/like")
  @Operation(summary = "Get the likes of a post")
  @APIResponses({
      @APIResponse(responseCode = "200", description = "Likes retrieved"),
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
  List<UserResponse> getLikes (@PathParam("id") @NotNull @Valid String postId);

  @GET
  @Path("/user/{id}/follower")
  @Operation(summary = "Get the followers of a user")
  @APIResponses({
          @APIResponse(responseCode = "200", description = "Followers retrieved"),
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
  List<UserResponse> getFollowers (@PathParam("id") @NotNull @Valid String userId);


  @GET
  @Path("/user/{id}/following")
  @Operation(summary = "Get the users followed by a user")
  @APIResponses({
          @APIResponse(responseCode = "200", description = "Users followed retrieved"),
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
  List<UserResponse> getFollowings (@PathParam("id") @NotNull @Valid String userId);

  @GET
  @Path("/user/block")
  @Operation(summary = "Get the users blocked")
  @APIResponses({
          @APIResponse(responseCode = "200", description = "Users blocked retrieved"),
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
  List<UserResponse> getBlocks ();
}
