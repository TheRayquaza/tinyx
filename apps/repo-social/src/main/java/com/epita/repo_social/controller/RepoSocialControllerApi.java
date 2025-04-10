package com.epita.repo_social.controller;

import com.epita.exchange.utils.Logger;
import com.epita.repo_social.RepoSocialErrorCode;
import com.epita.repo_social.controller.response.PostResponse;
import com.epita.repo_social.controller.response.UserResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
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
  Response likePost(@PathParam("postId") @NotNull @Valid String postId);

  @DELETE
  @Path("/post/{postId}/like")
  @Operation(summary = "Deletes the like of a post")
  @APIResponses({
          @APIResponse(responseCode = "204", description = "Like deleted"),
          @APIResponse(
                  responseCode = "400",
                  description = "Invalid input",
                  content = @Content(schema = @Schema(implementation = RepoSocialErrorCode.class))),
          @APIResponse(
                  responseCode = "401",
                  description = "User not authorized",
                  content = @Content(schema = @Schema(implementation = RepoSocialErrorCode.class))),
          @APIResponse(
                  responseCode = "404",
                  description = "Like not found",
                  content = @Content(schema = @Schema(implementation = RepoSocialErrorCode.class))),
          @APIResponse(
                  responseCode = "500",
                  description = "Error during the cypher script execution",
                  content = @Content(schema = @Schema(implementation = RepoSocialErrorCode.class))),
  })
  @NotNull
  Response unlikePost(@PathParam("postId") @NotNull @Valid String postId);

  @POST
  @Path("/user/{userId}/follow")
  @Operation(summary = "Follows another user")
  @APIResponses({
          @APIResponse(responseCode = "201", description = "Follow created"),
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
                  description = "User not found",
                  content = @Content(schema = @Schema(implementation = RepoSocialErrorCode.class))),
          @APIResponse(
                  responseCode = "500",
                  description = "Error during the cypher script execution",
                  content = @Content(schema = @Schema(implementation = RepoSocialErrorCode.class))),
  })
  @NotNull
  Response followUser(@PathParam("userId") @NotNull @Valid String userId);

  @DELETE
  @Path("/user/{userId}/follow")
  @Operation(summary = "Unfollows another user")
  @APIResponses({
          @APIResponse(responseCode = "204", description = "Follow deleted"),
          @APIResponse(
                  responseCode = "400",
                  description = "Invalid input",
                  content = @Content(schema = @Schema(implementation = RepoSocialErrorCode.class))),
          @APIResponse(
                  responseCode = "401",
                  description = "User not authorized",
                  content = @Content(schema = @Schema(implementation = RepoSocialErrorCode.class))),
          @APIResponse(
                  responseCode = "404",
                  description = "Not following this user",
                  content = @Content(schema = @Schema(implementation = RepoSocialErrorCode.class))),
          @APIResponse(
                  responseCode = "500",
                  description = "Error during the cypher script execution",
                  content = @Content(schema = @Schema(implementation = RepoSocialErrorCode.class))),
  })
  @NotNull
  Response unfollowUser(@PathParam("userId") @NotNull @Valid String userId);

  @POST
  @Path("/user/{userId}/block")
  @Operation(summary = "Blocks another user")
  @APIResponses({
          @APIResponse(responseCode = "201", description = "Block created"),
          @APIResponse(
                  responseCode = "400",
                  description = "Invalid input",
                  content = @Content(schema = @Schema(implementation = RepoSocialErrorCode.class))),
          @APIResponse(
                  responseCode = "401",
                  description = "User not authorized",
                  content = @Content(schema = @Schema(implementation = RepoSocialErrorCode.class))),
          @APIResponse(
                  responseCode = "404",
                  description = "User not found",
                  content = @Content(schema = @Schema(implementation = RepoSocialErrorCode.class))),
          @APIResponse(
                  responseCode = "500",
                  description = "Error during the cypher script execution",
                  content = @Content(schema = @Schema(implementation = RepoSocialErrorCode.class))),
  })
  @NotNull
  Response blockUser(@PathParam("userId") @NotNull @Valid String userId);

  @DELETE
  @Path("/user/{userId}/block")
  @Operation(summary = "Unblocks another user")
  @APIResponses({
          @APIResponse(responseCode = "204", description = "Block deleted"),
          @APIResponse(
                  responseCode = "400",
                  description = "Invalid input",
                  content = @Content(schema = @Schema(implementation = RepoSocialErrorCode.class))),
          @APIResponse(
                  responseCode = "401",
                  description = "User not authorized",
                  content = @Content(schema = @Schema(implementation = RepoSocialErrorCode.class))),
          @APIResponse(
                  responseCode = "404",
                  description = "Not blocked this user",
                  content = @Content(schema = @Schema(implementation = RepoSocialErrorCode.class))),
          @APIResponse(
                  responseCode = "500",
                  description = "Error during the cypher script execution",
                  content = @Content(schema = @Schema(implementation = RepoSocialErrorCode.class))),
  })
  @NotNull
  Response unblockUser(@PathParam("userId") @NotNull @Valid String userId);

  @GET
  @Path("/post/{postId}/like")
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
                  description = "Post not found",
                  content = @Content(schema = @Schema(implementation = RepoSocialErrorCode.class))),
          @APIResponse(
                  responseCode = "500",
                  description = "Error during the cypher script execution",
                  content = @Content(schema = @Schema(implementation = RepoSocialErrorCode.class))),
  })
  @NotNull
  List<UserResponse> getLikes(@PathParam("postId") @NotNull @Valid String postId);

  @GET
  @Path("/user/{userId}/like")
    @Operation(summary = "Get the posts liked by a user")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Posts liked retrieved"),
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
                    description = "User not found",
                    content = @Content(schema = @Schema(implementation = RepoSocialErrorCode.class))),
            @APIResponse(
                    responseCode = "500",
                    description = "Error during the cypher script execution",
                    content = @Content(schema = @Schema(implementation = RepoSocialErrorCode.class))),
    })
    @NotNull
    List<PostResponse> getUserLikedPosts(@PathParam("userId") @NotNull @Valid String userId);

  @GET
  @Path("/user/{userId}/follower")
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
                  description = "User not found",
                  content = @Content(schema = @Schema(implementation = RepoSocialErrorCode.class))),
          @APIResponse(
                  responseCode = "500",
                  description = "Error during the cypher script execution",
                  content = @Content(schema = @Schema(implementation = RepoSocialErrorCode.class))),
  })
  @NotNull
  List<UserResponse> getFollowers(@PathParam("userId") @NotNull @Valid String userId);


  @GET
  @Path("/user/{userId}/following")
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
                  description = "User not found",
                  content = @Content(schema = @Schema(implementation = RepoSocialErrorCode.class))),
          @APIResponse(
                  responseCode = "500",
                  description = "Error during the cypher script execution",
                  content = @Content(schema = @Schema(implementation = RepoSocialErrorCode.class))),
  })
  @NotNull
  List<UserResponse> getFollowings(@PathParam("userId") @NotNull @Valid String userId);

  @GET
  @Path("/user/{userId}/blocked")
  @Operation(summary = "Get the users blocked by a user")
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
                  description = "Forbidden - can only see own blocked list",
                  content = @Content(schema = @Schema(implementation = RepoSocialErrorCode.class))),
          @APIResponse(
                  responseCode = "404",
                  description = "User not found",
                  content = @Content(schema = @Schema(implementation = RepoSocialErrorCode.class))),
          @APIResponse(
                  responseCode = "500",
                  description = "Error during the cypher script execution",
                  content = @Content(schema = @Schema(implementation = RepoSocialErrorCode.class))),
  })
  @NotNull
  List<UserResponse> getBlocked(@PathParam("userId") @NotNull @Valid String userId);

  @GET
  @Path("/user/{userId}/blockedBy")
  @Operation(summary = "Get the users who blocked a user")
  @APIResponses({
          @APIResponse(responseCode = "200", description = "Users who blocked retrieved"),
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
                  description = "Forbidden - can only see own blocked-by list",
                  content = @Content(schema = @Schema(implementation = RepoSocialErrorCode.class))),
          @APIResponse(
                  responseCode = "404",
                  description = "User not found",
                  content = @Content(schema = @Schema(implementation = RepoSocialErrorCode.class))),
          @APIResponse(
                  responseCode = "500",
                  description = "Error during the cypher script execution",
                  content = @Content(schema = @Schema(implementation = RepoSocialErrorCode.class))),
  })
  @NotNull
  List<UserResponse> getBlockedBy(@PathParam("userId") @NotNull @Valid String userId);
}
