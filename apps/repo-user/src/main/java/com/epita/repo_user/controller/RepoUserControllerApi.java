package com.epita.repo_user.controller;

import com.epita.exchange.utils.Logger;
import com.epita.repo_user.RepoUserErrorCode;
import com.epita.repo_user.controller.request.CreateUserRequest;
import com.epita.repo_user.controller.request.LoginRequest;
import com.epita.repo_user.controller.request.ModifyUserRequest;
import com.epita.repo_user.controller.request.UploadImageRequest;
import com.epita.repo_user.controller.response.UserLoginResponse;
import com.epita.repo_user.service.entity.UserEntity;
import io.quarkus.security.Authenticated;
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

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("/")
public interface RepoUserControllerApi extends Logger {
  @POST
  @Path("/login")
  @Operation(summary = "Login user credentials")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "User JWT",
        content = @Content(schema = @Schema(implementation = UserEntity.class))),
    @APIResponse(
        responseCode = "400",
        description = "Invalid input",
        content = @Content(schema = @Schema(implementation = RepoUserErrorCode.class))),
    @APIResponse(
        responseCode = "401",
        description = "User not authorized",
        content = @Content(schema = @Schema(implementation = RepoUserErrorCode.class))),
    @APIResponse(
        responseCode = "404",
        description = "User Not Found",
        content = @Content(schema = @Schema(implementation = RepoUserErrorCode.class))),
  })
  @NotNull
  UserLoginResponse login(@RequestBody(required = true) @NotNull @Valid LoginRequest request);

  @POST
  @Path("/user")
  @Operation(summary = "Create a new user account")
  @APIResponses({
    @APIResponse(
        responseCode = "201",
        description = "User account created successfully",
        content = @Content(schema = @Schema(implementation = UserEntity.class))),
    @APIResponse(
        responseCode = "400",
        description = "Invalid input",
        content = @Content(schema = @Schema(implementation = RepoUserErrorCode.class))),
    @APIResponse(
        responseCode = "409",
        description = "Username already exists",
        content = @Content(schema = @Schema(implementation = RepoUserErrorCode.class)))
  })
  UserEntity createUser(@RequestBody(required = true) @NotNull @Valid CreateUserRequest request);

  @PUT
  @Path("/user")
  @Authenticated
  @Operation(summary = "Modify my user's account")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "User account updated successfully",
        content = @Content(schema = @Schema(implementation = UserEntity.class))),
    @APIResponse(
        responseCode = "400",
        description = "Invalid input",
        content = @Content(schema = @Schema(implementation = RepoUserErrorCode.class))),
    @APIResponse(
        responseCode = "404",
        description = "User not found",
        content = @Content(schema = @Schema(implementation = RepoUserErrorCode.class)))
  })
  UserEntity modifyUser(@RequestBody(required = true) @NotNull @Valid ModifyUserRequest request);

  @PUT
  @Path("/user/image")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Authenticated
  @Operation(summary = "Upload Profile image for my user's account")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "User account updated successfully",
        content = @Content(schema = @Schema(implementation = UserEntity.class))),
    @APIResponse(
        responseCode = "400",
        description = "Invalid input",
        content = @Content(schema = @Schema(implementation = RepoUserErrorCode.class))),
    @APIResponse(
        responseCode = "404",
        description = "User not found",
        content = @Content(schema = @Schema(implementation = RepoUserErrorCode.class)))
  })
  UserEntity uploadProfileImage(
      @RequestBody(required = true) @NotNull @Valid UploadImageRequest request);

  @DELETE
  @Path("/user")
  @Operation(summary = "Delete my user's account")
  @APIResponses({
    @APIResponse(responseCode = "204", description = "User account deleted successfully"),
    @APIResponse(
        responseCode = "404",
        description = "User not found",
        content = @Content(schema = @Schema(implementation = RepoUserErrorCode.class)))
  })
  void deleteUser();

  @GET
  @Path("/user")
  @Authenticated
  @Operation(summary = "Get my user's account")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Current user account retrieved successfully",
        content = @Content(schema = @Schema(implementation = UserEntity.class))),
    @APIResponse(
        responseCode = "404",
        description = "User not found",
        content = @Content(schema = @Schema(implementation = RepoUserErrorCode.class)))
  })
  UserEntity getCurrentUserAccount();

  @GET
  @Path("/user/{id}")
  @Operation(summary = "Get a specific user's account")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "User account retrieved successfully",
        content = @Content(schema = @Schema(implementation = UserEntity.class))),
    @APIResponse(
        responseCode = "404",
        description = "User not found",
        content = @Content(schema = @Schema(implementation = RepoUserErrorCode.class)))
  })
  UserEntity getUserAccount(@PathParam("id") String id);
}
