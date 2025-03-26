package com.epita.repo_user.converter;

import com.epita.exchange.utils.Converter;
import com.epita.repo_user.repository.model.UserModel;
import com.epita.repo_user.service.entity.UserEntity;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserModelToUserEntityConverter implements Converter<UserModel, UserEntity> {
  @Override
  public UserEntity convertNotNull(UserModel input) {
    return new UserEntity()
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
