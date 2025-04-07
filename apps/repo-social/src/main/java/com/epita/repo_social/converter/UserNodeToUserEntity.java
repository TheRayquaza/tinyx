package com.epita.repo_social.converter;

import com.epita.exchange.utils.Converter;
import com.epita.repo_social.controller.response.UserResponse;
import com.epita.repo_social.repository.model.UserNode;
import com.epita.repo_social.service.entity.UserEntity;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserNodeToUserEntity implements Converter<UserNode, UserEntity> {
    @Override
    public UserEntity convertNotNull(UserNode input) {
        return new UserEntity()
                .withId(input.userId())
                .withUsername(input.username())
                .withEmail(input.email())
                .withBio(input.bio())
                .withProfileImage(input.profileImage())
                .withCreatedAt(input.createdAt())
                .withUpdatedAt(input.updatedAt())
                .withDeleted(input.deleted());
    }
}
