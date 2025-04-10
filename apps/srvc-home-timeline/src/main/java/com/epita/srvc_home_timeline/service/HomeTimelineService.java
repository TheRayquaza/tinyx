package com.epita.srvc_home_timeline.service;

import com.epita.exchange.redis.aggregate.PostAggregate;
import com.epita.srvc_home_timeline.HomeTimelineErrorCode;
import com.epita.srvc_home_timeline.controller.response.HomeTimelineResponse;
import com.epita.srvc_home_timeline.converter.HomeTimelineEntityToHomeTimelineModel;
import com.epita.srvc_home_timeline.converter.HomeTimelineModelToHomeTimelineEntity;
import com.epita.srvc_home_timeline.converter.HomeTimelinePostModelToHomeTimelinePostEntity;
import com.epita.srvc_home_timeline.repository.HomeTimelinePostRepository;
import com.epita.srvc_home_timeline.repository.HomeTimelineRepository;
import com.epita.srvc_home_timeline.repository.model.HomeTimelineModel;
import com.epita.srvc_home_timeline.repository.model.HomeTimelinePostModel;
import com.epita.srvc_home_timeline.service.entity.HomeTimelineEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.jboss.logging.Logger;

@ApplicationScoped
public class HomeTimelineService {
  @Inject HomeTimelineRepository homeTimelineRepository;
  @Inject HomeTimelinePostRepository postRepository;
  @Inject HomeTimelineModelToHomeTimelineEntity homeTimelineModelToEntity;
  @Inject HomeTimelinePostModelToHomeTimelinePostEntity homeTimelinePostModelToEntity;
  @Inject HomeTimelineEntityToHomeTimelineModel homeTimelineEnityToModel;
  @Inject HomeTimelinePostRepository homeTimelinePostRepository;
  @Inject Logger logger;

  HomeTimelineModel initHomeTimeline(String userId) {
    HomeTimelineModel homeTimelineModel = new HomeTimelineModel();
    homeTimelineModel.setCreatedAt(LocalDateTime.now());
    homeTimelineModel.setUserId(userId);
    homeTimelineModel.setEntries(new ArrayList<>());
    homeTimelineModel.setFollowersId(new ArrayList<>());
    homeTimelineModel.setBlockedUsersId(new ArrayList<>());
    return homeTimelineModel;
  }

  HomeTimelinePostModel initHomeTimelinePost(PostAggregate postAggregate) {
    return new HomeTimelinePostModel()
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
  }

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
      Optional<HomeTimelinePostModel> toDelete =
          homeTimelinePostRepository.findByPostId(postAggregate.getId());
      if (!toDelete.isPresent()) {
        return;
      }
      homeTimelinePostRepository.deleteModel(toDelete.get());
      homeTimelineDelete(postAggregate.getId());
    } else {
      homeTimelinePostRepository.create(initHomeTimelinePost(postAggregate));
      homeTimelineAdd(postAggregate);
    }
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
      if (homeTimelineModel.getBlockedUsersId().contains(postAggregate.getOwnerId())) {
        continue;
      }

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

  public void handleFollow(String userId, String followerId) {
    logger.info(
        "received follow with user id: %s and follower id: %s".formatted(userId, followerId));
    Optional<HomeTimelineModel> homeTimeline = homeTimelineRepository.findByUserId(userId);
    if (homeTimeline.isPresent()) {
      HomeTimelineModel homeTimelineModel = homeTimeline.get();
      List<String> followers = homeTimelineModel.getFollowersId();
      if (!followers.contains(followerId)) {
        followers.add(followerId);
      }
      homeTimelineModel.setFollowersId(followers);
      homeTimelineRepository.updateModel(homeTimelineModel);
    } else {
      logger.error("user %s does not exist".formatted(userId));
    }
  }

  public void handleUnfollow(String userId, String followerId) {
    Optional<HomeTimelineModel> homeTimeline = homeTimelineRepository.findByUserId(userId);
    if (homeTimeline.isPresent()) {
      HomeTimelineModel homeTimelineModel = homeTimeline.get();
      HomeTimelineEntity homeTimelineEntity =
          homeTimelineModelToEntity.convertNotNull(homeTimelineModel);
      List<String> followers = homeTimelineEntity.getFollowersId();
      if (followers.contains(followerId)) {
        followers.remove(followerId);
      }
      homeTimelineEntity.setFollowersId(followers);
      List<HomeTimelineEntity.HomeTimelineEntryEntity> newEntries =
          homeTimelineEntity.getEntries().stream()
              .filter(entry -> entry.getAuthorId().equals(followerId))
              .collect(Collectors.toList());
      homeTimelineEntity.setEntries(newEntries);
      for (HomeTimelineEntity.HomeTimelineEntryEntity homeTimelineEntryEntity :
          homeTimelineEntity.getEntries()) {
        if (homeTimelineEntryEntity.getLikedBy().contains(followerId)) {
          homeTimelineEntryEntity.setLikedBy(
              homeTimelineEntryEntity.getLikedBy().stream()
                  .filter(likedBy -> !likedBy.equals(followerId))
                  .collect(Collectors.toList()));
        }
      }
      homeTimelineRepository.updateModel(
          homeTimelineEnityToModel.convertNotNull(homeTimelineEntity));
    } else {
      // FIXME Shouldn't happen handle error
    }
  }

  public void handleBlock(String userId, String blockedUserId) {
    Optional<HomeTimelineModel> homeTimeline = homeTimelineRepository.findByUserId(userId);
    if (homeTimeline.isPresent()) {
      HomeTimelineModel homeTimelineModel = homeTimeline.get();

      List<String> blockedUsers = homeTimelineModel.getBlockedUsersId();
      if (!blockedUsers.contains(blockedUserId)) {
        blockedUsers.add(blockedUserId);
      }
      homeTimelineModel.setBlockedUsersId(blockedUsers);

      List<HomeTimelineModel.HomeTimelineEntryModel> entries =
          homeTimelineModel.getEntries().stream()
              .filter(entry -> !entry.getAuthorId().equals(blockedUserId))
              .toList();

      entries.forEach(
          entry -> {
            entry.setLikedBy(
                entry.getLikedBy().stream()
                    .filter(likedBy -> !likedBy.getUserId().equals(blockedUserId))
                    .toList());
            if (entry.getLikedBy().isEmpty()
                && !homeTimelineModel.getFollowersId().contains(entry.getAuthorId())) {
              entries.remove(entry);
            }
          });

      homeTimelineModel.setEntries(entries);

      homeTimelineRepository.updateModel(homeTimelineModel);
    } else {
      HomeTimelineModel newModel = initHomeTimeline(userId);
      List<String> blockedUsers = newModel.getBlockedUsersId();
      blockedUsers.add(blockedUserId);
      newModel.setBlockedUsersId(blockedUsers);
      homeTimelineRepository.create(newModel);
    }
  }

  public void handleUnblock(String userId, String blockedUserId) {
    Optional<HomeTimelineModel> homeTimeline = homeTimelineRepository.findByUserId(userId);
    if (homeTimeline.isPresent()) {
      HomeTimelineModel homeTimelineModel = homeTimeline.get();
      List<String> blockedUsers = homeTimelineModel.getBlockedUsersId();
      if (blockedUsers.contains(blockedUserId)) {
        blockedUsers.remove(blockedUserId);
      }
      homeTimelineModel.setBlockedUsersId(blockedUsers);
      homeTimelineRepository.updateModel(homeTimelineModel);
    } else {
      HomeTimelineModel newModel = initHomeTimeline(blockedUserId);
      homeTimelineRepository.create(newModel);
    }
  }

  public void handleLike(String UserId, String postId) {
    Optional<HomeTimelinePostModel> homeTimelinePostModel =
        homeTimelinePostRepository.findByPostId(postId);
    if (homeTimelinePostModel.isEmpty()) {
      // maybe error because there is a like of a post that doesn't exist
      return;
    }
    String followerId = homeTimelinePostModel.get().getOwnerId();
    List<HomeTimelineModel> hometimelinesModel =
        homeTimelineRepository.getHomeTimelineContainingUserId(UserId, followerId);
    HomeTimelinePostModel PostModel = homeTimelinePostModel.get();
    for (HomeTimelineModel homeTimelineModel : hometimelinesModel) {
      HomeTimelineEntity homeTimelineEntity =
          homeTimelineModelToEntity.convertNotNull(homeTimelineModel);
      List<HomeTimelineEntity.HomeTimelineEntryEntity> entries = homeTimelineEntity.getEntries();
      List<HomeTimelineEntity.HomeTimelineLikedByEntity> likedBy = new ArrayList<>();
      HomeTimelineEntity.HomeTimelineLikedByEntity likedByEntity =
          new HomeTimelineEntity.HomeTimelineLikedByEntity()
              .withUserId(UserId)
              .withLikedAt(LocalDateTime.now());
      likedBy.add(likedByEntity);
      HomeTimelineEntity.HomeTimelineEntryEntity toAdd =
          new HomeTimelineEntity.HomeTimelineEntryEntity()
              .withPostId(postId)
              .withAuthorId(PostModel.getOwnerId())
              .withContent(PostModel.getText())
              .withLikedBy(likedBy)
              .withType(PostModel.getMedia())
              .withTimestamp(LocalDateTime.now());
      entries.add(toAdd);
      homeTimelineEntity.setEntries(entries);
      homeTimelineRepository.updateModel(
          homeTimelineEnityToModel.convertNotNull(homeTimelineEntity));
    }
  }

  public void handleUnlike(String UserId, String postId) {
    List<HomeTimelineModel> homeTimelinesModel =
        homeTimelineRepository.getHomeTimelineContainingPostId(postId);
    for (HomeTimelineModel homeTimelineModel : homeTimelinesModel) {
      HomeTimelineEntity homeTimelineEntity =
          homeTimelineModelToEntity.convertNotNull(homeTimelineModel);
      List<HomeTimelineEntity.HomeTimelineEntryEntity> entries = homeTimelineEntity.getEntries();
      for (HomeTimelineEntity.HomeTimelineEntryEntity entry : entries) {
        if (entry.getLikedBy().stream().anyMatch(likedBy -> likedBy.getUserId().equals(UserId))) {
          entry.setLikedBy(
              entry.getLikedBy().stream()
                  .filter(likedBy -> !likedBy.getUserId().equals(UserId))
                  .toList());
        }
      }
      homeTimelineEntity.setEntries(entries);
      homeTimelineEntity.setEntries(
          homeTimelineEntity.getEntries().stream()
              .filter(
                  entry ->
                      !(entry.getLikedBy().size() == 0
                          && !homeTimelineEntity.getFollowersId().contains(entry.getAuthorId())))
              .toList());
      homeTimelineRepository.updateModel(
          homeTimelineEnityToModel.convertNotNull(homeTimelineEntity));
    }
  }

  public void handleUserDeletion(String userId) {
    Optional<HomeTimelineModel> homeTimeline = homeTimelineRepository.findByUserId(userId);
    if (homeTimeline.isPresent()) {
      HomeTimelineModel toDelete = homeTimeline.get();
      homeTimelineRepository.deleteModel(toDelete);
    }
  }

  public void handleUserCreation(String userId) {
    logger.info("received user creation for user %s".formatted(userId));
    Optional<HomeTimelineModel> homeTimeline = homeTimelineRepository.findByUserId(userId);
    if (homeTimeline.isPresent()) {
      // FIXME handle error (it shouldn't happen)
    } else {
      homeTimelineRepository.create(initHomeTimeline(userId));
    }
  }
}
