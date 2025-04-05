package com.epita.srvc_home_timeline.controller.response;

import com.epita.exchange.utils.Converter;
import jakarta.enterprise.context.ApplicationScoped;
import com.epita.srvc_home_timeline.service.entity.HomeTimelineEntity;
import com.epita.srvc_home_timeline.repository.model.HomeTimelineModel;

import java.io.Serializable;

@ApplicationScoped
public class HomeTimelineModelToHomeTimelineEntity implements Converter<HomeTimelineModel, HomeTimelineEntity> {
    @Override
    public HomeTimelineEntity convertNotNull(HomeTimelineModel input) {
        return new HomeTimelineEntity()
                .withId(input.getId())
                .withUserId(input.getUserId())
                .withEntries(input.getEntries())
                .withFollowersId(intput.getFollowersId())
    }
}
