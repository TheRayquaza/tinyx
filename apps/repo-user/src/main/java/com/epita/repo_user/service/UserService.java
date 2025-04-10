package com.epita.repo_user.service;

import com.epita.exchange.auth.service.AuthService;
import com.epita.exchange.redis.aggregate.UserAggregate;
import com.epita.exchange.redis.service.RedisPublisher;
import com.epita.exchange.s3.service.S3Service;
import com.epita.exchange.utils.Logger;
import com.epita.repo_user.RepoUserErrorCode;
import com.epita.repo_user.controller.request.CreateUserRequest;
import com.epita.repo_user.controller.request.LoginRequest;
import com.epita.repo_user.controller.request.ModifyUserRequest;
import com.epita.repo_user.controller.request.UploadImageRequest;
import com.epita.repo_user.controller.response.UserLoginResponse;
import com.epita.repo_user.converter.UserModelToUserAggregateConverter;
import com.epita.repo_user.converter.UserModelToUserEntityConverter;
import com.epita.repo_user.repository.UserRepository;
import com.epita.repo_user.repository.model.UserModel;
import com.epita.repo_user.service.entity.UserEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.util.*;
import org.bson.types.ObjectId;
import org.mindrot.jbcrypt.BCrypt;

@ApplicationScoped
public class UserService implements Logger {

  @Inject UserRepository userRepository;

  @Inject UserModelToUserEntityConverter userModelToUserEntity;

  @Inject UserModelToUserAggregateConverter userModelToUserAggregate;

  @Inject UserModelToUserEntityConverter userModelToUserEntityConverter;

  @Inject S3Service s3Service;

  @Inject RedisPublisher redisPublisher;

  String userAggregateChannel =
      System.getenv().getOrDefault("USER_AGGREGATE_CHANNEL", "user_channel");

  public UserLoginResponse login(LoginRequest request) {
    if (request == null || request.username == null || request.password == null) {
      throw RepoUserErrorCode.INVALID_USER_DATA.createError("request / username / password");
    }

    UserModel userModel =
        userRepository
            .findByUsername(request.username)
            .orElseThrow(
                () -> RepoUserErrorCode.USER_WITH_USERNAME_FOUND.createError(request.username));

    if (!BCrypt.checkpw(request.password, userModel.getPasswordHash()))
      throw RepoUserErrorCode.UNAUTHORIZED.createError();

    return new UserLoginResponse(
        userModel.getId().toString(),
        userModel.getUsername(),
        AuthService.generateToken(userModel.getId().toString(), userModel.getUsername()));
  }

  // @Transactional
  public UserEntity updateUser(ObjectId id, ModifyUserRequest request) {
    if (request == null) throw RepoUserErrorCode.INVALID_USER_DATA.createError("request");

    UserModel userModel =
        userRepository
            .findByIdOptional(id)
            .orElseThrow(() -> RepoUserErrorCode.USER_NOT_FOUND.createError(id.toString()));

    if (request.username != null && !Objects.equals(request.username, userModel.getUsername())) {
      if (userRepository.findByUsername(request.username).isPresent())
        throw RepoUserErrorCode.USER_WITH_USERNAME_FOUND.createError(request.username);
      logger()
          .info(
              "User '{}' ('{}') changes its username to '{}'",
              userModel.getUsername(),
              userModel.getId(),
              request.username);
      userModel.setUsername(request.username);
    }

    if (request.bio != null) userModel.setBio(request.bio);

    if (request.email != null) userModel.setEmail(request.email);

    userModel.setUpdatedAt(LocalDateTime.now());

    userRepository.update(userModel);
    logger().info("New User model registered: '{}'", userModel.getId());

    redisPublisher.publish(
        userAggregateChannel, userModelToUserAggregate.convertNotNull(userModel));
    logger().info("New User Aggregate pushed to Redis: '{}'", userModel.getId());

    return userModelToUserEntity.convertNotNull(userModel);
  }

  // @Transactional
  public UserEntity createUser(CreateUserRequest request) {
    UserModel userModel = new UserModel();

    if (request.username != null) {
      if (userRepository.findByUsername(request.username).stream().findFirst().isPresent())
        throw RepoUserErrorCode.USER_WITH_USERNAME_ALREADY_EXISTS.createError("username");
      userModel.setUsername(request.username);
    }

    if (request.email != null) {
      userModel.setEmail(request.email);
    } else {
      throw RepoUserErrorCode.INVALID_USER_DATA.createError("email");
    }

    userModel.setProfileImage(null);

    LocalDateTime now = LocalDateTime.now();
    userModel.setCreatedAt(now);
    userModel.setUpdatedAt(now);
    userModel.setPasswordHash(BCrypt.hashpw(request.password, BCrypt.gensalt()));

    userRepository.persist(userModel);

    redisPublisher.publish(
        userAggregateChannel, userModelToUserAggregate.convertNotNull(userModel));
    return userModelToUserEntity.convertNotNull(userModel);
  }

  public UserEntity getUser(ObjectId id) {
    Optional<UserModel> userModel = userRepository.findByIdOptional(id);
    if (userModel.isPresent()) return userModelToUserEntity.convertNotNull(userModel.get());
    throw RepoUserErrorCode.USER_NOT_FOUND.createError();
  }

  // @Transactional
  public void deleteUser(ObjectId id) {
    UserModel userModel =
        userRepository
            .findByIdOptional(id)
            .orElseThrow(() -> RepoUserErrorCode.USER_NOT_FOUND.createError(id.toString()));
    userModel.setDeleted(true);
    UserAggregate userAggregate = userModelToUserAggregate.convertNotNull(userModel);
    userRepository.deleteById(id);
    redisPublisher.publish(userAggregateChannel, userAggregate);
  }

  // @Transactional
  public UserEntity uploadProfileImage(ObjectId userId, UploadImageRequest request) {
    String objectKey = "user/" + userId + "/image/" + UUID.randomUUID() + ".jpeg";

    UserModel userModel =
        userRepository
            .findByIdOptional(userId)
            .orElseThrow(() -> RepoUserErrorCode.USER_NOT_FOUND.createError(userId));
    try {
      byte[] bytes = request.getFile().readAllBytes();
      int size = bytes.length;
      objectKey = s3Service.uploadFile(objectKey, new ByteArrayInputStream(bytes), size);
      if (userModel.getProfileImage() != null) {
        s3Service.deleteFile(userModel.getProfileImage());
      }
      userModel.setProfileImage(objectKey);
      userRepository.update(userModel);
      redisPublisher.publish(
          userAggregateChannel, userModelToUserAggregate.convertNotNull(userModel));
      return userModelToUserEntityConverter.convertNotNull(userModel);
    } catch (Exception e) {
      throw RepoUserErrorCode.INTERNAL_SERVER_ERROR.createError();
    }
  }
}
