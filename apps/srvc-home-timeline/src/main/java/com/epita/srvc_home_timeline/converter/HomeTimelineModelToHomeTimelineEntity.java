package com.epita.srvc_home_timeline.converter;

import com.epita.exchange.utils.Converter;
import com.epita.srvc_home_timeline.repository.model.HomeTimelineModel;
import com.epita.srvc_home_timeline.service.entity.HomeTimelineEntity;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class HomeTimelineModelToHomeTimelineEntity
    implements Converter<HomeTimelineModel, HomeTimelineEntity> {
  @Override
  public HomeTimelineEntity convertNotNull(HomeTimelineModel input) {
    List<HomeTimelineEntity.HomeTimelineEntryEntity> convertedEntries = null;
    if (input.getEntries() != null) {
      convertedEntries =
          input.getEntries().stream()
              .map(entry -> convertEntry(entry))
              .collect(Collectors.toList());
    }
    HomeTimelineEntity res =
        new HomeTimelineEntity()
            .withId(input.getId())
            .withUserId(input.getUserId())
            .withFollowersId(input.getFollowersId())
            .withBlockedUsersId(input.getBlockedUsersId())
            .withEntries(convertedEntries);
    return res;
  }

  private HomeTimelineEntity.HomeTimelineEntryEntity convertEntry(
      HomeTimelineModel.HomeTimelineEntryModel entry) {
    if (entry == null) {
      return null;
    }

    List<HomeTimelineEntity.HomeTimelineLikedByEntity> convertedLikedBy = null;
    if (entry.getLikedBy() != null) {
      convertedLikedBy =
          entry.getLikedBy().stream()
              .map(like -> convertLikedBy(like))
              .collect(Collectors.toList());
    }

    return new HomeTimelineEntity.HomeTimelineEntryEntity()
        .withPostId(entry.getPostId())
        .withAuthorId(entry.getAuthorId())
        .withContent(entry.getContent())
        .withLikedBy(convertedLikedBy)
        .withType(entry.getType())
        .withTimestamp(entry.getTimestamp());
  }

  private HomeTimelineEntity.HomeTimelineLikedByEntity convertLikedBy(
      HomeTimelineModel.HomeTimelineLikedByModel entry) {
    if (entry == null) {
      return null;
    }

    return new HomeTimelineEntity.HomeTimelineLikedByEntity()
        .withUserId(entry.getUserId())
        .withLikedAt(entry.getLikedAt());
  }
}
