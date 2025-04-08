package com.epita.srvc_home_timeline.converter;

import com.epita.exchange.utils.Converter;
import com.epita.srvc_home_timeline.repository.model.HomeTimelinePostModel;
import com.epita.srvc_home_timeline.service.entity.HomeTimelinePostEntity;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class HomeTimelinePostModelToHomeTimelinePostEntity implements Converter<HomeTimelinePostModel, HomeTimelinePostEntity> {
    @Override
    public HomeTimelinePostEntity convertNotNull(HomeTimelinePostModel input) {
        return new HomeTimelinePostEntity()
                .withId(input.getId())
                .withPostId(input.getPostId())
                .withOwnerId(input.getOwnerId())
                .withText(input.getText())
                .withMedia(input.getMedia())
                .withRepostIdString(input.getRepostId())
                .withReplyToPostId(input.getReplyToPostId())
                .withReply(input.isReply())
                .withCreatedAt(input.getCreatedAt())
                .withUpdatedAt(input.getUpdatedAt())
                .withDeleted(input.isDeleted());
    }
}
