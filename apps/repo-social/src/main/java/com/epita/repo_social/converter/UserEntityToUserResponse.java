package com.epita.repo_social.converter;

import com.epita.exchange.utils.Converter;
import com.epita.repo_social.controller.response.UserResponse;
import com.epita.repo_social.service.entity.UserEntity;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserEntityToUserResponse implements Converter<UserEntity, UserResponse> {
    @Override
    public UserResponse convertNotNull(UserEntity input) {
        return new UserResponse()
                .withId(input.getId())
                .withUsername(input.getUsername())
                .withEmail(input.getEmail())
                .withBio(input.getBio())
                .withProfileImage(input.getProfileImage())
                .withCreatedAt(input.getCreatedAt())
                .withUpdatedAt(input.getUpdatedAt())
                .withDeleted(input.isDeleted());
    }
}
