package com.epita.repo_post.service;

import com.epita.exchange.s3.service.S3Service;
import com.epita.repo_post.RepoPostErrorCode;
import com.epita.repo_post.controller.request.CreatePostRequest;
import com.epita.repo_post.converter.PostModelToPostEntity;
import com.epita.repo_post.repository.PostRepository;
import com.epita.repo_post.repository.model.PostModel;
import com.epita.repo_post.service.entity.PostEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class PostService {

    @Inject
    PostRepository postRepository;

    @Inject
    PostModelToPostEntity postModelToPostEntity;

    @Inject
    S3Service s3Service;

    public PostEntity createPost(CreatePostRequest request) {
        if (request == null || request.ownerId == null) {
            throw RepoPostErrorCode.INVALID_POST_DATA.createError("request / ownerId");
        }

        PostModel postModel = new PostModel();

        // TODO: Check ownerId exists
        postModel.setOwnerId(request.ownerId);
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
}
