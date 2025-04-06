package com.epita.srvc_search.converter;

import com.epita.exchange.redis.aggregate.PostAggregate;
import com.epita.exchange.utils.Converter;
import com.epita.srvc_search.service.entity.SearchEntity;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PostAggregateToSearchEntity implements Converter<PostAggregate, SearchEntity> {
  @Override
  public SearchEntity convertNotNull(PostAggregate input) {
    return new SearchEntity()
        .withId(input.getId())
        .withOwnerId(input.getOwnerId())
        .withMedia(input.getMedia())
        .withText(input.getText())
        .withCreatedAt(input.getCreatedAt())
        .withUpdatedAt(input.getUpdatedAt())
        .withDeleted(input.isDeleted());
  }
}
