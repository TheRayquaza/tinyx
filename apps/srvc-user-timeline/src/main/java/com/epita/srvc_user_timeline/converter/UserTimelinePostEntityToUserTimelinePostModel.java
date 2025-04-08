package com.epita.srvc_user_timeline.converter;

import com.epita.srvc_user_timeline.repository.model.UserTimelinePostModel;
import com.epita.srvc_user_timeline.service.entity.UserTimelinePostEntity;
import com.epita.exchange.utils.Converter;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserTimelinePostEntityToUserTimelinePostModel implements Converter<UserTimelinePostEntity, UserTimelinePostModel> {

    @Override
    public UserTimelinePostModel convertNotNull(UserTimelinePostEntity userTimelinePostEntity) {
        return new UserTimelinePostModel().withUserId(userTimelinePostEntity.getUserId()).withId(userTimelinePostEntity.getId())
                .withOwnerId(userTimelinePostEntity.getOwnerId())
                .withIsReply(userTimelinePostEntity.getIsReply())
                .withMedia(userTimelinePostEntity.getMedia())
                .withCreatedAt(userTimelinePostEntity.getCreatedAt())
                .withUpdatedAt(userTimelinePostEntity.getUpdatedAt())
                .withText(userTimelinePostEntity.getText())
                .withReplyToPostId(userTimelinePostEntity.getReplyToPostId())
                .withDeleted(userTimelinePostEntity.isDeleted())
                .withLikedAt(userTimelinePostEntity.getLikedAt())
                .withRepostId(userTimelinePostEntity.getRepostId());
    }
}
