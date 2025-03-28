package com.epita.repo_post.converter;

import com.epita.exchange.utils.Converter;
import com.epita.repo_post.repository.model.PostModel;
import com.epita.repo_post.service.entity.PostEntity;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PostModelToPostEntity implements Converter<PostModel, PostEntity> {
  @Override
  public PostEntity convertNotNull(PostModel input) {
    return new PostEntity()
        .withId(input.getId())
        .withOwnerId(input.getOwnerId())
        .withMedia(input.getMedia())
        .withText(input.getText())
        .withCreatedAt(input.getCreatedAt())
        .withUpdatedAt(input.getUpdatedAt())
        .withDeleted(input.isDeleted());
  }
}
