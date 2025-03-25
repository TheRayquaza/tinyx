package com.epita.repo_user.service;

import com.epita.exchange.redis.service.RedisPublisher;
import com.epita.repo_user.RepoUserErrorCode;
import com.epita.repo_user.controller.RepoUserControllerApi;
import com.epita.repo_user.controller.request.CreateUserRequest;
import com.epita.repo_user.controller.request.LoginRequest;
import com.epita.repo_user.controller.request.ModifyUserRequest;
import com.epita.repo_user.service.entity.UserEntity;
import com.epita.repo_user.converter.UserModelToUserAggregateConverter;
import com.epita.repo_user.converter.UserModelToUserEntityConverter;
import com.epita.repo_user.repository.UserRepository;
import com.epita.repo_user.repository.model.UserModel;
import io.quarkus.redis.datasource.RedisDataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.bson.types.ObjectId;
import org.mindrot.jbcrypt.BCrypt;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@ApplicationScoped
public class UserService {
    @Inject
    UserRepository userRepository;

    @Inject
    UserModelToUserEntityConverter userModelToUserEntity;

    @Inject
    UserModelToUserAggregateConverter userModelToUserAggregate;

    RedisDataSource redisDataSource;

    RedisPublisher redisPublisher = new RedisPublisher(redisDataSource.);

    public UserEntity login(LoginRequest request) {
        if (request == null || request.username == null || request.password == null) {
            throw RepoUserErrorCode.INVALID_USER_DATA.createError("request / username / password");
        }

        UserModel userModel = userRepository.findByUsername(request.username)
                .orElseThrow(() -> RepoUserErrorCode.USER_NOT_FOUND.createError(request.username));

        if (!BCrypt.checkpw(request.password, userModel.getPasswordHash())) {
            throw RepoUserErrorCode.UNAUTHORIZED.createError();
        }

        return userModelToUserEntity.convertNotNull(userModel);
    }

    @Transactional
    public UserEntity updateUser(ObjectId id, ModifyUserRequest request) {
        if (request == null) {
            throw RepoUserErrorCode.INVALID_USER_DATA.createError("request");
        }

        UserModel userModel = userRepository.findByIdOptional(id)
                .orElseThrow(() -> RepoUserErrorCode.USER_NOT_FOUND.createError(id.toString()));

        if (request.username != null && !Objects.equals(request.username, userModel.getUsername())) {
            if (userRepository.findByUsername(request.username).isPresent())
                throw RepoUserErrorCode.USER_ALREADY_EXISTS.createError(request.username);
            userModel.setUsername(request.username);
        }

        if (request.bio != null)
            userModel.setBio(request.bio);

        if (request.email != null)
            userModel.setEmail(request.email);

        userModel.setUpdatedAt(LocalDateTime.now());

        return userModelToUserEntity.convertNotNull(userModel);
    }

    @Transactional
    public UserEntity createUser(CreateUserRequest request) {
        UserModel userModel = new UserModel();

        if (request.username != null) {
            if (userRepository.findByUsername(request.username).stream().findFirst().isPresent()) {
                throw RepoUserErrorCode.USER_ALREADY_EXISTS.createError("username");
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

        redisPublisher.publish("user_aggregate", userModelToUserAggregate.convertNotNull(userModel));
        return userModelToUserEntity.convertNotNull(userModel);
    }

    public UserEntity getUser(ObjectId id) {
        Optional<UserModel> userModel = userRepository.findByIdOptional(id);
        if (userModel.isPresent())
            return userModelToUserEntity.convertNotNull(userModel.get());
        throw RepoUserErrorCode.USER_NOT_FOUND.createError();
    }

    public void deleteUser(ObjectId id) {
        Optional<UserModel> userModel = userRepository.findByIdOptional(id);
        if (userModel.isEmpty())
            throw RepoUserErrorCode.USER_NOT_FOUND.createError(id.toString());
        userRepository.deleteById(id);
        redisPublisher.publish("user_aggregate", userModelToUserAggregate.convertNotNull(userModel.get()));
    }
}
