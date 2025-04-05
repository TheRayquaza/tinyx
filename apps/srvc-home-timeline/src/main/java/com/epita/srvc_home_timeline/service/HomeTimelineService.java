package com.epita.srvc_home_timeline.service;

import com.epita.srvc_home_timeline.converter.HomeTimelineModelToHomeTimelineEntity;
import com.epita.srvc_home_timeline.repository.HomeTimelineRepository;
import com.epita.srvc_home_timeline.repository.model.HomeTimelineModel;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class HomeTimelineService {
  @Inject HomeTimelineRepository homeTimelineRepository;
  @Inject HomeTimelineModelToHomeTimelineEntity converter;

  // handle the subscription to the follow redis
  public void newFollower(String UserId, String followerId) {
    Optional<HomeTimelineModel> homeTimeline = homeTimelineRepository.findByUserId(UserId);
    if (homeTimeline.isPresent()) {
      HomeTimelineModel homeTimelineModel = homeTimeline.get();
      List<String> followers = homeTimelineModel.getFollowersId();
      if (!followers.contains(followerId)) {
        followers.add(followerId);
      }
      homeTimelineModel.setFollowersId(followers);
    } else {
      HomeTimelineModel newModel = new HomeTimelineModel();
      newModel.setUserId(UserId);
      newModel.setCreatedAt(LocalDateTime.now());
      List<String> followers = new ArrayList<>();
      followers.add(followerId);
      newModel.setFollowersId(followers);
      homeTimelineRepository.create(newModel);
    }
  }
}
