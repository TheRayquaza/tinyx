package com.epita.repo_post.converter;

import com.epita.exchange.redis.aggregate.PostAggregate;
import com.epita.exchange.utils.Converter;
import com.epita.repo_post.repository.model.PostModel;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PostModelToPostAggregate implements Converter<PostModel, PostAggregate> {
    @Override
    public PostAggregate convertNotNull(PostModel input) {
        return new PostAggregate()
                .withId(input.getId().toString())
                .withOwnerId(input.getOwnerId())
                .withMedia(input.getMedia())
                .withText(input.getText())
                .withCreatedAt(input.getCreatedAt())
                .withUpdatedAt(input.getUpdatedAt())
                .withDeleted(input.isDeleted());
    }
}
