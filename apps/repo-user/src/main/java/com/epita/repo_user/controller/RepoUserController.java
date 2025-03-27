package com.epita.repo_user.controller;

import com.epita.exchange.auth.service.AuthService;
import com.epita.repo_user.controller.request.CreateUserRequest;
import com.epita.repo_user.controller.request.LoginRequest;
import com.epita.repo_user.controller.request.ModifyUserRequest;
import com.epita.repo_user.controller.request.UploadImageRequest;
import com.epita.repo_user.controller.response.UserLoginResponse;
import com.epita.repo_user.service.UserService;
import com.epita.repo_user.service.entity.UserEntity;
import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.bson.types.ObjectId;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("/")
public class RepoUserController implements RepoUserControllerApi {

  @Inject AuthService authService;

  @Inject UserService userService;

  @POST
  @Path("/login")
  @Override
  public @NotNull UserLoginResponse login(
      @RequestBody(required = true) @NotNull @Valid LoginRequest request) {
    logger().info("POST /login - username {}", request.username);
    return userService.login(request);
  }

  @POST
  @Path("/user")
  @Override
  public @NotNull UserEntity createUser(
      @RequestBody(required = true) @NotNull @Valid CreateUserRequest request) {
    logger().info("POST /user - username {}", request.username);
    return userService.createUser(request);
  }

  @PUT
  @Path("/user")
  @Authenticated
  @Override
  public UserEntity modifyUser(
      @RequestBody(required = true) @NotNull @Valid ModifyUserRequest request) {
    String userId = authService.getUserId();
    logger().info("PUT /user/{}", userId);
    return userService.updateUser(new ObjectId(userId), request);
  }

  @POST
  @Path("/user/image")
  @Authenticated
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Override
  public @NotNull UserEntity uploadProfileImage(
      @RequestBody(required = true) @NotNull @Valid UploadImageRequest request) {
    String userId = authService.getUserId();
    logger().info("PUT /user/image - {}", userId);
    return userService.uploadProfileImage(new ObjectId(userId), request);
  }

  @DELETE
  @Path("/user/{id}")
  @Authenticated
  @Override
  public void deleteUser() {
    String userId = authService.getUserId();
    logger().info("DELETE /user with id {}", userId);
    userService.deleteUser(new ObjectId(userId));
  }

  @GET
  @Path("/user")
  @Authenticated
  @Override
  public @NotNull UserEntity getCurrentUserAccount() {
    String userId = authService.getUserId();
    logger().info("GET /user");
    return userService.getUser(new ObjectId(userId));
  }

  @GET
  @Path("/user/{id}")
  @Authenticated
  @Override
  public UserEntity getUserAccount(@PathParam("id") String id) {
    logger().info("GET /user/{}", id);
    return userService.getUser(new ObjectId(id));
  }
}
