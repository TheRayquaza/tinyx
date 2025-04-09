package com.epita.repo_post.service;

import com.epita.exchange.auth.service.AuthService;
import com.epita.exchange.redis.command.BlockCommand;
import com.epita.exchange.redis.service.RedisPublisher;
import com.epita.exchange.s3.service.S3Service;
import com.epita.exchange.utils.Logger;
import com.epita.repo_post.RepoPostErrorCode;
import com.epita.repo_post.controller.request.CreatePostRequest;
import com.epita.repo_post.controller.request.EditPostRequest;
import com.epita.repo_post.controller.request.PostReplyRequest;
import com.epita.repo_post.controller.response.AllRepliesResponse;
import com.epita.repo_post.converter.PostModelToPostAggregate;
import com.epita.repo_post.converter.PostModelToPostEntity;
import com.epita.repo_post.repository.BlockedRepository;
import com.epita.repo_post.repository.PostRepository;
import com.epita.repo_post.repository.model.BlockedModel;
import com.epita.repo_post.repository.model.PostModel;
import com.epita.repo_post.service.entity.PostEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.bson.types.ObjectId;

@ApplicationScoped
public class PostService implements Logger {

  @Inject PostRepository postRepository;

  @Inject BlockedRepository blockedRepository;

  @Inject PostModelToPostEntity postModelToPostEntity;

  @Inject S3Service s3Service;

  @Inject AuthService authService;

  @Inject RedisPublisher redisPublisher;

  @Inject PostModelToPostAggregate postModelToPostAggregate;

  String postAggregateChannel =
      System.getenv().getOrDefault("POST_AGGREGATE_CHANNEL", "post-channel");

  public PostEntity createPost(CreatePostRequest request, String ownerId) {
    if (request == null
        || ((request.media == null || request.extension == null) && request.text == null)) {
      logger().error("Invalid post data");
      throw RepoPostErrorCode.INVALID_POST_DATA.createError(
          "request / ownerId / media / text is null: {}", request.toString());
    }

    if (!authService.getUserId().equals(ownerId)) {
      throw RepoPostErrorCode.FORBIDDEN.createError("auth user is not provided ownerId");
    }

    PostModel postModel = new PostModel();
    ObjectId post_id = new ObjectId();
    postModel.setId(post_id);

    postModel.setOwnerId(ownerId);

    if (request.media != null) {
      String objectKey = "post/" + post_id + "/image/" + UUID.randomUUID() + ".jpeg";
      try {
        objectKey =
            s3Service.uploadFile(objectKey, request.getMedia(), request.getMedia().available());
        logger().info("Uploaded media to S3: {}", objectKey);
      } catch (Exception e) {
        logger().error("Failed to upload media to S3", e);
        throw RepoPostErrorCode.INTERNAL_SERVER_ERROR.createError();
      }
      postModel.setMedia(objectKey);
    }

    if (request.text != null) {
      postModel.setText(request.text);
    }

    postModel.setCreatedAt(LocalDateTime.now());
    postModel.setUpdatedAt(LocalDateTime.now());
    postModel.setDeleted(false);
    postModel.setIsReply(false);

    // Persist the new data
    postRepository.create(postModel);
    logger().info("Post persisted with id: {}", post_id);

    // Persist to Redis
    redisPublisher.publish(
        postAggregateChannel, postModelToPostAggregate.convertNotNull(postModel));
    logger().info("Published post to Redis: {}", post_id);

    return postModelToPostEntity.convertNotNull(postModel);
  }

  public PostEntity getPostById(String postId) {
    PostModel postModel =
        postRepository
            .findByIdStringOptional(postId)
            .orElseThrow(() -> RepoPostErrorCode.POST_NOT_FOUND.createError(postId));

    // Check if user authorized to access post
    String userId = authService.getUserId();
    String ownerId = postModel.getOwnerId();
    List<BlockedModel> blocked = blockedRepository.findIfBlocked(ownerId, userId);
    if (!blocked.isEmpty()) {
      throw RepoPostErrorCode.FORBIDDEN.createError(
          "User {} was blocked by the User {} - Cannot access post {}", userId, ownerId, postId);
    }

    return postModelToPostEntity.convertNotNull(postModel);
  }

  // @Transactional
  public PostEntity editPost(EditPostRequest request, String postId) {
    if (request == null
        || ((request.media == null || request.extension == null) && request.text == null)) {
      logger().error("Invalid post data");
      throw RepoPostErrorCode.INVALID_POST_DATA.createError(
          "request / ownerId / media / text is null");
    }

    PostModel postModel =
        postRepository
            .findByIdStringOptional(postId)
            .orElseThrow(() -> RepoPostErrorCode.POST_NOT_FOUND.createError(postId));

    if (!authService.getUserId().equals(postModel.getOwnerId())) {
      logger()
          .error(
              "Forbidden edit attempt: user={}, postOwner={}",
              authService.getUserId(),
              postModel.getOwnerId());
      throw RepoPostErrorCode.FORBIDDEN.createError("auth user is not the owner of the post");
    }

    if (request.text != null) {
      postModel.setText(request.text);
    }

    if (request.media != null) {
      String objectKey = "post/" + postId + "/image/" + UUID.randomUUID() + "." + request.extension;
      try {
        if (postModel.getMedia() != null) {
          s3Service.deleteFile(postModel.getMedia());
          logger().info("Deleted old media from S3: {}", postModel.getMedia());
        }
        objectKey =
            s3Service.uploadFile(objectKey, request.getMedia(), request.getMedia().available());
        logger().info("Uploaded new media to S3: {}", objectKey);
      } catch (Exception e) {
        logger().error("Media update failed", e);
        throw RepoPostErrorCode.INTERNAL_SERVER_ERROR.createError();
      }
      postModel.setMedia(objectKey);
    }
    postModel.setUpdatedAt(LocalDateTime.now());

    postRepository.update(postModel);

    redisPublisher.publish(
        postAggregateChannel, postModelToPostAggregate.convertNotNull(postModel));
    return postModelToPostEntity.convertNotNull(postModel);
  }

  // @Transactional
  public void deletePost(String postId) {
    PostModel postModel =
        postRepository
            .findByIdStringOptional(postId)
            .orElseThrow(() -> RepoPostErrorCode.POST_NOT_FOUND.createError(postId));

    if (!authService.getUserId().equals(postModel.getOwnerId())) {
      logger()
          .error(
              "Forbidden delete attempt: user={}, postOwner={}",
              authService.getUserId(),
              postModel.getOwnerId());
      throw RepoPostErrorCode.FORBIDDEN.createError("auth user is not the owner of the post");
    }

    // Check post is not already deleted
    if (postModel.isDeleted()) {
      logger().warn("Post already deleted: {}", postId);
      throw RepoPostErrorCode.POST_NOT_FOUND.createError(postId);
    }

    if (postModel.getMedia() != null) {
      s3Service.deleteFile(postModel.getMedia());
      postModel.setMedia(null);
    }

    postModel.setDeleted(true);

    postRepository.update(postModel);
    logger().info("Post marked as deleted in DB: {}", postId);

    redisPublisher.publish(
        postAggregateChannel, postModelToPostAggregate.convertNotNull(postModel));
    logger().info("Published deleted post to Redis: {}", postId);
  }

  public PostEntity replyToPost(PostReplyRequest request, String postId) {
    logger().info("Replying to postId: {}, with request: {}", postId, request);
    if (request == null
        || ((request.media == null || request.extension == null) && request.text == null)) {
      logger().error("Invalid post data");
      throw RepoPostErrorCode.INVALID_POST_DATA.createError(
          "request / ownerId / media / text is null");
    }

    // To check if the post we are replying to exists
    PostModel og_post =
        postRepository
            .findByIdStringOptional(postId)
            .orElseThrow(() -> RepoPostErrorCode.POST_NOT_FOUND.createError(postId));

    // Check if user authorized to access post
    String userId = authService.getUserId();
    String ownerId = og_post.getOwnerId();
    List<BlockedModel> blocked = blockedRepository.findIfBlocked(ownerId, userId);
    if (!blocked.isEmpty()) {
      throw RepoPostErrorCode.FORBIDDEN.createError(
          "User {} was blocked by the User {} - Cannot access post {}", userId, ownerId, postId);
    }

    // Create the reply
    PostModel reply = new PostModel();
    reply.setOwnerId(userId);
    reply.setText(request.getText());
    reply.setIsReply(true);
    reply.setReplyToPostId(postId);
    reply.setCreatedAt(LocalDateTime.now());
    reply.setUpdatedAt(LocalDateTime.now());

    if (request.getText() != null) {
      reply.setText(request.getText());
    }

    postRepository.create(reply);

    redisPublisher.publish(postAggregateChannel, postModelToPostAggregate.convertNotNull(reply));
    logger().info("Published reply to Redis for post: {}", postId);

    return postModelToPostEntity.convertNotNull(reply);
  }

  public AllRepliesResponse getAllRepliesForPost(String postId) {

    PostModel og_post =
        postRepository
            .findByIdStringOptional(postId)
            .orElseThrow(() -> RepoPostErrorCode.POST_NOT_FOUND.createError(postId));

    // Check if user authorized to access post
    String userId = authService.getUserId();
    String ownerId = og_post.getOwnerId();
    List<BlockedModel> blocked = blockedRepository.findIfBlocked(ownerId, userId);
    if (!blocked.isEmpty()) {
      throw RepoPostErrorCode.FORBIDDEN.createError(
          "User {} was blocked by the User {} - Cannot access post {}", userId, ownerId, postId);
    }

    List<PostModel> replies = postRepository.findAllReplies(postId);
    logger().info("Found {} replies for postId: {}", replies.size(), postId);
    return new AllRepliesResponse()
        .withReplies(
            replies.stream()
                .map((postModel) -> postModelToPostEntity.convertNotNull(postModel))
                .toList());
  }

  public void updateBlockedPeople(BlockCommand message) {
    BlockedModel model = new BlockedModel();
    model.setBlocked(message.isBlocked());
    model.setTargetId(message.getTargetId());
    model.setUserId(message.getUserId());
    blockedRepository.create(model);
  }
}
