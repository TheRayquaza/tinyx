package com.epita.repo_social.converter;

import com.epita.exchange.utils.Converter;
import com.epita.repo_social.repository.model.PostNode;
import com.epita.repo_social.service.entity.PostEntity;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PostNodeToPostEntity implements Converter<PostNode, PostEntity> {
    @Override
    public PostEntity convertNotNull(PostNode input) {
        return new PostEntity()
                .withId(input.postId())
                .withOwnerId(input.ownerId())
                .withText(input.text())
                .withMedia(input.media())
                .withRepostId(input.repostId())
                .withReplyToPostId(input.replyToPostId())
                .withIsReply(input.isReply())
                .withCreatedAt(input.createdAt())
                .withUpdatedAt(input.updatedAt())
                .withDeleted(input.deleted());
    }
}
