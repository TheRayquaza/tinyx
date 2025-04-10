package com.epita.srvc_user_timeline.converter;

import com.epita.exchange.utils.Converter;
import com.epita.srvc_user_timeline.controller.contract.UserTimelineResponse;
import com.epita.srvc_user_timeline.service.entity.UserTimelineEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class UserTimelineEntityToUserTimelineResponse
    implements Converter<UserTimelineEntity, UserTimelineResponse> {
  @Inject UserTimelinePostEntityToUserTimelinePostResponse utpEntityToUtpResponse;

  @Override
  public UserTimelineResponse convertNotNull(UserTimelineEntity input) {
    return new UserTimelineResponse()
        .withPosts(
            input.getPosts().stream().map(p -> utpEntityToUtpResponse.convertNotNull(p)).toList());
  }
}
