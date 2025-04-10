package com.epita.repo_social.converter;

import com.epita.exchange.utils.Converter;
import com.epita.repo_social.controller.response.PostResponse;
import com.epita.repo_social.repository.model.PostNode;
import com.epita.repo_social.service.entity.PostEntity;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PostEntityToPostResponse implements Converter<PostEntity, PostResponse> {
    @Override
    public PostResponse convertNotNull(PostEntity input) {
        return new PostResponse()
                .withId(input.getId())
                .withOwnerId(input.getOwnerId())
                .withText(input.getText())
                .withMedia(input.getMedia())
                .withRepostId(input.getRepostId())
                .withReplyToPostId(input.getReplyToPostId())
                .withIsReply(input.getIsReply())
                .withCreatedAt(input.getCreatedAt())
                .withUpdatedAt(input.getUpdatedAt())
                .withDeleted(input.isDeleted());
    }
}
