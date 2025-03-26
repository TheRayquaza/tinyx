package com.epita.repo_user.controller;

import com.epita.exchange.auth.service.AuthContext;
import com.epita.exchange.auth.service.AuthService;
import com.epita.repo_user.controller.request.CreateUserRequest;
import com.epita.repo_user.controller.request.LoginRequest;
import com.epita.repo_user.controller.request.ModifyUserRequest;
import com.epita.repo_user.controller.request.UploadImageRequest;
import com.epita.repo_user.service.UserService;
import com.epita.repo_user.service.entity.UserEntity;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.bson.types.ObjectId;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

public class RepoUserController implements RepoUserControllerApi {

  @Inject AuthContext authContext;

  @Inject UserService userService;

  @Override
  public UserEntity login(@RequestBody(required = true) @NotNull @Valid LoginRequest request) {
    logger().info("POST /login - username {}", request.username);
    return userService.login(request);
  }

  @Override
  public UserEntity createUser(
      @RequestBody(required = true) @NotNull @Valid CreateUserRequest request) {
    logger().info("POST /user - username {}", request.username);
    return userService.createUser(request);
  }

  @Override
  public UserEntity modifyUser(
      @RequestBody(required = true) @NotNull @Valid ModifyUserRequest request) {
    String userId = authContext.getAuthEntity().getUserId();
    logger().info("PUT /user/{}", userId);
    return userService.updateUser(new ObjectId(userId), request);
  }

  @Override
  public UserEntity uploadProfileImage(
      @RequestBody(required = true) @NotNull @Valid UploadImageRequest request) {
    String userId = authContext.getAuthEntity().getUserId();
    logger().info("PUT /user/image - {}", userId);
    return userService.uploadProfileImage(new ObjectId(userId), request);
  }

  @Override
  public void deleteUser() {
    String userId = authContext.getAuthEntity().getUserId();
    logger().info("DELETE /user with id {}", userId);
    userService.deleteUser(new ObjectId(userId));
  }

  @Override
  public UserEntity getCurrentUserAccount() {
    String userId = authContext.getAuthEntity().getUserId();
    logger().info("GET /user");
    return userService.getUser(new ObjectId(userId));
  }

  @Override
  public UserEntity getUserAccount(@NotNull String id) {
    logger().info("GET /user/{}", id);
    return userService.getUser(new ObjectId(id));
  }
}
