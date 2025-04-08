package com.epita.srvc_user_timeline.converter;

import com.epita.srvc_user_timeline.controller.contract.UserTimelinePostResponse;
import com.epita.srvc_user_timeline.service.entity.UserTimelinePostEntity;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserTimelinePostEntityToUserTimelinePostResponse implements Converter<UserTimelinePostEntity,UserTimelinePostResponse> {
  @Override
  public UserTimelinePostResponse convertNotNull(UserTimelinePostEntity input) {
    return new UserTimelinePostResponse()
        .withId(input.getId())
        .withUserId(input.getUserId())
        .withText(input.getText())
        .withMedia(input.getMedia())
        .withDeleted(input.isDeleted())
        .withCreatedAt(input.getCreatedAt())
        .withUpdatedAt(input.getUpdatedAt())
        .withOwnerId(input.getOwnerId())
        .withLikedAt(input.getLikedAt())
        .withIsReply(input.getIsReply())
        .withReplyToPostId(input.getReplyToPostId())
        .withRepostId(input.getRepostId());
  }
}
