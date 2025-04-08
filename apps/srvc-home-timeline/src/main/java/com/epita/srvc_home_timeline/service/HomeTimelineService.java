package com.epita.srvc_home_timeline.service;

import com.epita.exchange.redis.aggregate.PostAggregate;
import com.epita.srvc_home_timeline.converter.HomeTimelineEntityToHomeTimelineModel;
import com.epita.srvc_home_timeline.converter.HomeTimelineModelToHomeTimelineEntity;
import com.epita.srvc_home_timeline.converter.HomeTimelinePostModelToHomeTimelinePostEntity;
import com.epita.srvc_home_timeline.repository.HomeTimelinePostRepository;
import com.epita.srvc_home_timeline.repository.HomeTimelineRepository;
import com.epita.srvc_home_timeline.repository.model.HomeTimelineModel;
import com.epita.srvc_home_timeline.service.entity.HomeTimelineEntity;
import com.epita.srvc_home_timeline.repository.model.HomeTimelinePostModel;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class HomeTimelineService {
  @Inject HomeTimelineRepository homeTimelineRepository;
  @Inject HomeTimelinePostRepository postRepository;
  @Inject HomeTimelineModelToHomeTimelineEntity homeTimelineModelToEntity;
  @Inject HomeTimelinePostModelToHomeTimelinePostEntity homeTimelinePostModelToEntity;
  @Inject HomeTimelineEntityToHomeTimelineModel homeTimelineEnityToModel;
    @Inject
    HomeTimelinePostRepository homeTimelinePostRepository;

  public void handlePostAggregate(PostAggregate postAggregate) {
    if (postAggregate.isDeleted()) {
      homeTimelineRepository.delete("postId", postAggregate.getId());
      homeTimelineDelete(postAggregate.getId());
      return;
    }
    homeTimelineAdd(postAggregate);
  }

  public void homeTimelineDelete(String postId) {
    List<HomeTimelineModel> hometimelinesModel =
        homeTimelineRepository.getHomeTimelineContainingPostId(postId);
    for (HomeTimelineModel homeTimelineModel : hometimelinesModel) {
      HomeTimelineEntity homeTimelineEntity =
          homeTimelineModelToEntity.convertNotNull(homeTimelineModel);
      List<HomeTimelineEntity.HomeTimelineEntryEntity> entries = homeTimelineEntity.getEntries();
      HomeTimelineEntity.HomeTimelineEntryEntity toDelete = null;
      for (HomeTimelineEntity.HomeTimelineEntryEntity homeTimelineEntryEntity : entries) {
        if (homeTimelineEntryEntity.getPostId().equals(postId)) {
          toDelete = homeTimelineEntryEntity;
        }
      }
      if (toDelete != null) {
        entries.remove(toDelete);
      }
      homeTimelineRepository.updateModel(
          homeTimelineEnityToModel.convertNotNull(homeTimelineEntity));
    }
  }

  public void homeTimelineAdd(PostAggregate postAggregate) {
    List<HomeTimelineModel> hometimelinesModel =
        homeTimelineRepository.getHomeTimelineContainingUserId(postAggregate.getOwnerId());
    for (HomeTimelineModel homeTimelineModel : hometimelinesModel) {
      HomeTimelineEntity homeTimelineEntity =
          homeTimelineModelToEntity.convertNotNull(homeTimelineModel);
      List<HomeTimelineEntity.HomeTimelineEntryEntity> entries = homeTimelineEntity.getEntries();
      HomeTimelineEntity.HomeTimelineEntryEntity toAdd =
          new HomeTimelineEntity.HomeTimelineEntryEntity()
              .withPostId(postAggregate.getId())
              .withAuthorId(postAggregate.getOwnerId())
              .withContent(postAggregate.getText())
              .withLikedBy(new ArrayList<>())
              .withType(postAggregate.getMedia())
              .withTimestamp(LocalDateTime.now());
      entries.add(toAdd);
      homeTimelineRepository.updateModel(
          homeTimelineEnityToModel.convertNotNull(homeTimelineEntity));
    }
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

  public void handleLike(String UserId, String postId) {
    List<HomeTimelineModel> hometimelinesModel = homeTimelineRepository.getHomeTimelineContainingUserId(UserId);
    Optional<HomeTimelinePostModel> homeTimelinePostModel = homeTimelinePostRepository.findByPostId(postId);
    if (homeTimelinePostModel.isEmpty()) {
      // maybe error because there is a like of a post that doesn't exist
      return;
    }
    HomeTimelinePostModel PostModel = homeTimelinePostModel.get();
    for (HomeTimelineModel homeTimelineModel : hometimelinesModel) {
      HomeTimelineEntity homeTimelineEntity = homeTimelineModelToEntity.convertNotNull(homeTimelineModel);
      List<HomeTimelineEntity.HomeTimelineEntryEntity> entries = homeTimelineEntity.getEntries();
      List<HomeTimelineEntity.HomeTimelineLikedByEntity> likedBy = new ArrayList<>();
      HomeTimelineEntity.HomeTimelineLikedByEntity likedByEntity = new HomeTimelineEntity.HomeTimelineLikedByEntity()
              .withUserId(UserId)
              .withLikedAt(LocalDateTime.now());
      likedBy.add(likedByEntity);
      HomeTimelineEntity.HomeTimelineEntryEntity toAdd = new HomeTimelineEntity.HomeTimelineEntryEntity()
              .withPostId(postId)
              .withAuthorId(PostModel.getOwnerId())
              .withContent(PostModel.getText())
              .withLikedBy(likedBy)
              .withType(PostModel.getMedia())
              .withTimestamp(LocalDateTime.now());
      entries.add(toAdd);
      homeTimelineEntity.setEntries(entries);
      homeTimelineRepository.updateModel(homeTimelineEnityToModel.convertNotNull(homeTimelineEntity));
    }
  }

  public void handleUnlike(String UserId, String postId) {

  }
}
