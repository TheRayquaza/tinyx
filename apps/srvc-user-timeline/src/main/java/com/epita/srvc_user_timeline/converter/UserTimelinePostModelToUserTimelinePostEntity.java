package com.epita.srvc_user_timeline.converter;

import com.epita.exchange.utils.Converter;
import com.epita.srvc_user_timeline.repository.model.UserTimelinePostModel;
import com.epita.srvc_user_timeline.service.entity.UserTimelinePostEntity;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserTimelinePostModelToUserTimelinePostEntity implements Converter<UserTimelinePostModel, UserTimelinePostEntity> {
    @Override
    public UserTimelinePostEntity convertNotNull(UserTimelinePostModel input) {
            return new UserTimelinePostEntity()
                    .withId(input.getId())
                    .withPostId(input.getPostId())
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
