package com.epita.repo_post.service;

import com.epita.exchange.auth.service.AuthService;
import com.epita.exchange.redis.service.RedisPublisher;
import com.epita.repo_post.RepoPostErrorCode;
import com.epita.repo_post.controller.request.CreatePostRequest;
import com.epita.repo_post.controller.request.EditPostRequest;
import com.epita.repo_post.controller.request.PostReplyRequest;
import com.epita.repo_post.controller.response.AllRepliesResponse;
import com.epita.repo_post.converter.PostModelToPostAggregate;
import com.epita.repo_post.converter.PostModelToPostEntity;
import com.epita.repo_post.repository.PostRepository;
import com.epita.repo_post.repository.model.PostModel;
import com.epita.repo_post.service.entity.PostEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;
import org.bson.types.ObjectId;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class PostService {

  @Inject PostRepository postRepository;

  @Inject PostModelToPostEntity postModelToPostEntity;

  @Inject AuthService authService;

  @Inject
  RedisPublisher redisPublisher;

  @Inject
  PostModelToPostAggregate postModelToPostAggregate;

  @ConfigProperty(name = "repo.post.aggregate.channel")
  @Inject
  String postAggregateChannel;


  public PostEntity createPost(CreatePostRequest request, String ownerId) {
    if (request == null || ownerId == null || (request.media == null && request.text == null)) {
      throw RepoPostErrorCode.INVALID_POST_DATA.createError(
          "request / ownerId / media / text is null");
    }

    if (!authService.getUserId().equals(ownerId)) {
      throw RepoPostErrorCode.FORBIDDEN.createError("auth user is not provided ownerId");
    }

    PostModel postModel = new PostModel();

    postModel.setId(new ObjectId());

    postModel.setOwnerId(ownerId);

    if (request.media != null) {
      postModel.setMedia(request.media);
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

    redisPublisher.publish(postAggregateChannel, postModelToPostAggregate.convertNotNull(postModel));

    // Return the entity
    return postModelToPostEntity.convertNotNull(postModel);
  }

  public PostEntity getPostById(String postId) {
    PostModel postModel =
        postRepository
            .findByIdStringOptional(postId)
            .orElseThrow(() -> RepoPostErrorCode.POST_NOT_FOUND.createError(postId));
    return postModelToPostEntity.convertNotNull(postModel);
  }

  // @Transactional
  public PostEntity editPost(EditPostRequest request, String postId) {
    PostModel postModel =
        postRepository
            .findByIdStringOptional(postId)
            .orElseThrow(() -> RepoPostErrorCode.POST_NOT_FOUND.createError(postId));

    if (!authService.getUserId().equals(postModel.getOwnerId())) {
      throw RepoPostErrorCode.FORBIDDEN.createError("auth user is not the owner of the post");
    }

    if (request.text != null) {
      postModel.setText(request.text);
    }

    if (request.media != null) {
      postModel.setMedia(request.media);
    }
    postModel.setUpdatedAt(LocalDateTime.now());


    postRepository.update(postModel);

    redisPublisher.publish(postAggregateChannel, postModelToPostAggregate.convertNotNull(postModel));
    return postModelToPostEntity.convertNotNull(postModel);
  }

  // @Transactional
  public void deletePost(String postId) {
    PostModel postModel =
        postRepository
            .findByIdStringOptional(postId)
            .orElseThrow(() -> RepoPostErrorCode.POST_NOT_FOUND.createError(postId));

    if (!authService.getUserId().equals(postModel.getOwnerId())) {
      throw RepoPostErrorCode.FORBIDDEN.createError("auth user is not the owner of the post");
    }

    // Check post is not already deleted
    if (postModel.isDeleted()) {
      throw RepoPostErrorCode.POST_NOT_FOUND.createError(postId);
    }

    postModel.setDeleted(true);

    postRepository.update(postModel);

    redisPublisher.publish(postAggregateChannel, postModelToPostAggregate.convertNotNull(postModel));
  }

  public PostEntity replyToPost(PostReplyRequest request, String postId) {
    if (request == null || (request.getMedia() == null && request.getText() == null)) {
      throw RepoPostErrorCode.INVALID_POST_DATA.createError(
          "request / ownerId / media / text is null");
    }

    // To check if the post we are replying to exists
    PostModel og_post =
        postRepository
            .findByIdStringOptional(postId)
            .orElseThrow(() -> RepoPostErrorCode.POST_NOT_FOUND.createError(postId));

    // Create the reply
    PostModel reply = new PostModel();
    reply.setOwnerId(authService.getUserId());
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

    return postModelToPostEntity.convertNotNull(reply);
  }

  public AllRepliesResponse getAllRepliesForPost(String postId) {
    postRepository
        .findByIdStringOptional(postId)
        .orElseThrow(() -> RepoPostErrorCode.POST_NOT_FOUND.createError(postId));

    List<PostModel> replies = postRepository.findAllReplies(postId);
    return new AllRepliesResponse()
        .withReplies(
            replies.stream()
                .map((postModel) -> postModelToPostEntity.convertNotNull(postModel))
                .toList());
  }
}
