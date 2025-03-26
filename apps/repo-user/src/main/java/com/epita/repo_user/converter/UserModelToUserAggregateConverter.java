package com.epita.repo_user.converter;

import com.epita.exchange.redis.aggregate.UserAggregate;
import com.epita.exchange.utils.Converter;
import com.epita.repo_user.repository.model.UserModel;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserModelToUserAggregateConverter implements Converter<UserModel, UserAggregate> {
  @Override
  public UserAggregate convertNotNull(UserModel input) {
    return new UserAggregate()
        .withBio(input.getBio())
        .withId(input.getId())
        .withEmail(input.getEmail())
        .withDeleted(input.isDeleted())
        .withUsername(input.getUsername())
        .withProfileImage(input.getProfileImage())
        .withCreatedAt(input.getCreatedAt())
        .withUpdatedAt(input.getUpdatedAt());
  }
}
