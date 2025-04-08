package com.epita.srvc_home_timeline.service;

import com.epita.exchange.redis.aggregate.PostAggregate;
import com.epita.srvc_home_timeline.HomeTimelineErrorCode;
import com.epita.srvc_home_timeline.controller.response.HomeTimelineResponse;
import com.epita.srvc_home_timeline.converter.HomeTimelineModelToHomeTimelineEntity;
import com.epita.srvc_home_timeline.converter.HomeTimelinePostModelToHomeTimelinePostEntity;
import com.epita.srvc_home_timeline.repository.HomeTimelinePostRepository;
import com.epita.srvc_home_timeline.repository.HomeTimelineRepository;
import com.epita.srvc_home_timeline.repository.model.HomeTimelineModel;
import com.epita.srvc_home_timeline.repository.model.HomeTimelinePostModel;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.bson.types.ObjectId;

@ApplicationScoped
public class HomeTimelineService {
  @Inject HomeTimelineRepository homeTimelineRepository;
  @Inject HomeTimelinePostRepository postRepository;
  @Inject HomeTimelineModelToHomeTimelineEntity homeTimelineModelToEntity;
  @Inject HomeTimelinePostModelToHomeTimelinePostEntity homeTimelinePostModelToEntity;

  public HomeTimelineResponse getHomeTimelineById(String userId) {
    Optional<HomeTimelineModel> homeTimeline = homeTimelineRepository.findByUserId(userId);
    if (homeTimeline.isPresent()) {
      HomeTimelineModel homeTimelineModel = homeTimeline.get();
      homeTimelineModel.setEntries(
          homeTimelineModel.getEntries().stream()
              .sorted((entry1, entry2) -> entry2.getTimestamp().compareTo(entry1.getTimestamp()))
              .toList());
      return new HomeTimelineResponse(homeTimelineModelToEntity.convertNotNull(homeTimelineModel));
    } else {
      throw HomeTimelineErrorCode.USER_NOT_FOUND.createError(userId);
    }
  }

  public void handlePostAggregate(PostAggregate postAggregate) {
    if (postAggregate.isDeleted()) {
      homeTimelineRepository.delete("postId", postAggregate.getId());
      homeTimelineDelete(postAggregate.getId());
      return;
    }

    HomeTimelinePostModel homeTimelinePostModel =
        new HomeTimelinePostModel()
            .withId(new ObjectId())
            .withPostId(postAggregate.getId())
            .withOwnerId(postAggregate.getOwnerId())
            .withText(postAggregate.getText())
            .withMedia(postAggregate.getMedia())
            .withRepostId(postAggregate.getRepostId())
            .withReplyToPostId(postAggregate.getReplyToPostId())
            .withReply(postAggregate.isReply())
            .withCreatedAt(postAggregate.getCreatedAt())
            .withUpdatedAt(postAggregate.getUpdatedAt())
            .withDeleted(postAggregate.isDeleted());

    postRepository.create(homeTimelinePostModel);
  }

  public void homeTimelineDelete(String postId) {
    homeTimelineRepository.getHomeTimelineWithPostId(postId);
  }

  public void handleFollow(String UserId, String followerId) {
    Optional<HomeTimelineModel> homeTimeline = homeTimelineRepository.findByUserId(UserId);
    if (homeTimeline.isPresent()) {
      HomeTimelineModel homeTimelineModel = homeTimeline.get();
      List<String> followers = homeTimelineModel.getFollowersId();
      if (!followers.contains(followerId)) {
        followers.add(followerId);
      }
      homeTimelineModel.setFollowersId(followers);
      homeTimelineRepository.updateModel(homeTimelineModel);
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

  public void handleUnfollow(String UserId, String followerId) {
    Optional<HomeTimelineModel> homeTimeline = homeTimelineRepository.findByUserId(UserId);
    if (homeTimeline.isPresent()) {
      HomeTimelineModel homeTimelineModel = homeTimeline.get();
      List<String> followers = homeTimelineModel.getFollowersId();
      if (followers.contains(followerId)) {
        followers.remove(followerId);
      }
      homeTimelineModel.setFollowersId(followers);
      homeTimelineRepository.updateModel(homeTimelineModel);
    } else {
      HomeTimelineModel newModel = new HomeTimelineModel();
      newModel.setUserId(UserId);
      newModel.setCreatedAt(LocalDateTime.now());
      homeTimelineRepository.create(newModel);
    }
  }

  public void handleLike(String UserId, String followerId, String postId) {}

  public void handleUnlike(String UserId, String followerId, String postId) {}
}
