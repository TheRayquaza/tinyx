package com.epita.repo_post.service;

import com.epita.exchange.auth.service.AuthService;
import com.epita.repo_post.RepoPostErrorCode;
import com.epita.repo_post.controller.request.CreatePostRequest;
import com.epita.repo_post.controller.request.EditPostRequest;
import com.epita.repo_post.controller.request.PostReplyRequest;
import com.epita.repo_post.controller.response.AllRepliesResponse;
import com.epita.repo_post.converter.PostModelToPostEntity;
import com.epita.repo_post.repository.PostRepository;
import com.epita.repo_post.repository.model.PostModel;
import com.epita.repo_post.service.entity.PostEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.bson.types.ObjectId;

import java.util.List;

@ApplicationScoped
public class PostService {

  @Inject PostRepository postRepository;

  @Inject PostModelToPostEntity postModelToPostEntity;

  @Inject
  AuthService authService;

  public PostEntity createPost(CreatePostRequest request, String ownerId) {

    System.out.println("CREATE USER");
    if (request == null || ownerId == null || (request.media == null && request.text == null)) {
      throw RepoPostErrorCode.INVALID_POST_DATA.createError("request / ownerId / media / text is null");
    }

    PostModel postModel = new PostModel();

    postModel.setId(new ObjectId().toString());

    if (!authService.getUserId().equals(ownerId)) {
      throw RepoPostErrorCode.FORBIDDEN.createError("auth user is not provided ownerId");
    }

    postModel.setOwnerId(ownerId);
    if (request.media != null) {
      postModel.setMedia(request.media);
    }
    if (request.text != null) {
      postModel.setText(request.text);
    }

    // Persist the new data
    postRepository.create(postModel);

    // Return the entity
    return postModelToPostEntity.convertNotNull(postModel);
  }

  public PostEntity getPostById(String id) {
    PostModel postModel = postRepository.getById(id).orElseThrow(() -> RepoPostErrorCode.POST_NOT_FOUND.createError(id));
    return postModelToPostEntity.convertNotNull(postModel);
  }

  @Transactional
  public void editPost(EditPostRequest request, String postId) {
    PostModel postModel = postRepository.getById(postId).orElseThrow(() -> RepoPostErrorCode.POST_NOT_FOUND.createError(postId));

    if (!authService.getUserId().equals(postModel.getOwnerId())) {
      throw RepoPostErrorCode.FORBIDDEN.createError("auth user is not the owner of the post");
    }

    if (request.text != null) {
      postModel.setText(request.text);
    }

    if (request.media != null) {
      postModel.setMedia(request.media);
    }

    postRepository.update(postModel);
  }

  @Transactional
  public void deletePost(String postId) {
    PostModel postModel = postRepository.getById(postId).orElseThrow(() -> RepoPostErrorCode.POST_NOT_FOUND.createError(postId));
    if (!authService.getUserId().equals(postModel.getOwnerId())) {
      throw RepoPostErrorCode.FORBIDDEN.createError("auth user is not the owner of the post");
    }
    postModel.setDeleted(true);
    postRepository.update(postModel);
  }

  public void replyToPost(PostReplyRequest request, String postId) {
    PostModel postModel = postRepository.getById(postId).orElseThrow(() -> RepoPostErrorCode.POST_NOT_FOUND.createError(postId));
    postModel.setIsReply(true);
    postModel.setReplyToPostId(postId);
    postModel.setOwnerId(authService.getUserId());
    if (request.getText() != null) {
      postModel.setText(request.getText());
    }

    postRepository.create(postModel);
  }

  public AllRepliesResponse getAllRepliesForPost(String postId) {
    postRepository.getById(postId).orElseThrow(() -> RepoPostErrorCode.POST_NOT_FOUND.createError(postId));
    List<PostModel> replies = postRepository.findAllReplies(postId);
    return new AllRepliesResponse().withReplies(replies.stream().map((postModel) -> postModelToPostEntity.convertNotNull(postModel)).toList());
  }
}
