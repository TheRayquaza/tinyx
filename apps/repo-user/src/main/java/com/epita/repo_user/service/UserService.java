package com.epita.repo_user.service;

import com.epita.exchange.redis.aggregate.UserAggregate;
import com.epita.exchange.redis.service.RedisPublisher;
import com.epita.exchange.s3.service.S3Configuration;
import com.epita.exchange.s3.service.S3Service;
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
import jakarta.transaction.Transactional;
import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import org.apache.commons.io.FileUtils;
import org.bson.types.ObjectId;
import org.mindrot.jbcrypt.BCrypt;

@ApplicationScoped
public class UserService {

  @Inject UserRepository userRepository;

  @Inject UserModelToUserEntityConverter userModelToUserEntity;

  @Inject UserModelToUserAggregateConverter userModelToUserAggregate;

  @Inject UserModelToUserEntityConverter userModelToUserEntityConverter;

  @Inject S3Service s3Service;

  @Inject RedisPublisher redisPublisher;

  public UserLoginResponse login(LoginRequest request) {
    if (request == null || request.username == null || request.password == null) {
      throw RepoUserErrorCode.INVALID_USER_DATA.createError("request / username / password");
    }

    UserModel userModel =
        userRepository
            .findByUsername(request.username)
            .orElseThrow(() -> RepoUserErrorCode.USER_WITH_USERNAME_FOUND.createError(request.username));

    if (!BCrypt.checkpw(request.password, userModel.getPasswordHash())) {
      throw RepoUserErrorCode.UNAUTHORIZED.createError();
    }

    String token = userModel.getId() + "," + userModel.getUsername();
    return new UserLoginResponse(
        userModel.getId(),
        userModel.getUsername(),
        Arrays.toString(Base64.getDecoder().decode(token)));
  }

  @Transactional
  public UserEntity updateUser(ObjectId id, ModifyUserRequest request) {
    if (request == null) {
      throw RepoUserErrorCode.INVALID_USER_DATA.createError("request");
    }

    UserModel userModel =
        userRepository
            .findByIdOptional(id)
            .orElseThrow(() -> RepoUserErrorCode.USER_NOT_FOUND.createError(id.toString()));

    if (request.username != null && !Objects.equals(request.username, userModel.getUsername())) {
      if (userRepository.findByUsername(request.username).isPresent())
        throw RepoUserErrorCode.USER_ALREADY_EXISTS.createError(request.username);
      userModel.setUsername(request.username);
    }

    if (request.bio != null) userModel.setBio(request.bio);

    if (request.email != null) userModel.setEmail(request.email);

    userModel.setUpdatedAt(LocalDateTime.now());

    return userModelToUserEntity.convertNotNull(userModel);
  }

  @Transactional
  public UserEntity createUser(CreateUserRequest request) {
    UserModel userModel = new UserModel();

    if (request.username != null) {
      if (userRepository.findByUsername(request.username).stream().findFirst().isPresent()) {
        throw RepoUserErrorCode.USER_WITH_USERNAME_ALREADY_EXISTS.createError("username");
      }
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

    // redisPublisher.publish("user_aggregate", userModelToUserAggregate.convertNotNull(userModel));
    return userModelToUserEntity.convertNotNull(userModel);
  }

  public UserEntity getUser(ObjectId id) {
    Optional<UserModel> userModel = userRepository.findByIdOptional(id);
    if (userModel.isPresent()) return userModelToUserEntity.convertNotNull(userModel.get());
    throw RepoUserErrorCode.USER_NOT_FOUND.createError();
  }

  @Transactional
  public void deleteUser(ObjectId id) {
    UserModel userModel =
        userRepository
            .findByIdOptional(id)
            .orElseThrow(() -> RepoUserErrorCode.USER_NOT_FOUND.createError(id.toString()));
    userModel.setDeleted(true);
    UserAggregate userAggregate = userModelToUserAggregate.convertNotNull(userModel);
    userRepository.deleteById(id);
    redisPublisher.publish("user_aggregate", userAggregate);
  }

  @Transactional
  public UserEntity uploadProfileImage(ObjectId userId, UploadImageRequest request) {
    String objectKey = "user/" + userId + "/image/" + UUID.randomUUID() + ".jpeg";

    UserModel userModel =
        userRepository
            .findByIdOptional(userId)
            .orElseThrow(() -> RepoUserErrorCode.USER_NOT_FOUND.createError(userId));
    try {
      File tempFile = File.createTempFile("S3", userId.toString());
      tempFile.deleteOnExit();
      FileUtils.copyInputStreamToFile(request.getFile(), tempFile);
      s3Service.uploadFile(new S3Configuration(), objectKey, tempFile);
      s3Service.deleteFile(new S3Configuration(), userModel.getProfileImage());
      userModel.setProfileImage(objectKey);
      return userModelToUserEntityConverter.convertNotNull(userModel);
    } catch (Exception e) {
      throw RepoUserErrorCode.INTERNAL_SERVER_ERROR.createError();
    }
  }
}
