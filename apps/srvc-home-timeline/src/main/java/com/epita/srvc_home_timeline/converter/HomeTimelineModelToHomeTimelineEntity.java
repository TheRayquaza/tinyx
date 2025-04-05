package com.epita.srvc_home_timeline.converter;

import com.epita.exchange.utils.Converter;
import com.epita.srvc_home_timeline.repository.model.HomeTimelineModel;
import com.epita.srvc_home_timeline.service.entity.HomeTimelineEntity;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class HomeTimelineModelToHomeTimelineEntity
    implements Converter<HomeTimelineModel, HomeTimelineEntity> {
  @Override
  public HomeTimelineEntity convertNotNull(HomeTimelineModel input) {
    HomeTimelineEntity res =
        new HomeTimelineEntity()
            .withId(input.getId())
            .withUserId(input.getUserId())
            .withFollowersId(input.getFollowersId());
    // fix the code so that it can convert the list of nested struct to entity
    return res;
  }
}
