package com.epita.srvc_home_timeline.converter;

import com.epita.exchange.utils.Converter;
import com.epita.srvc_home_timeline.repository.model.HomeTimelineModel;
import com.epita.srvc_home_timeline.service.entity.HomeTimelineEntity;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class HomeTimelineEntityToHomeTimelineModel
    implements Converter<HomeTimelineEntity, HomeTimelineModel> {
  @Override
  public HomeTimelineModel convertNotNull(HomeTimelineEntity input) {
    List<HomeTimelineModel.HomeTimelineEntryModel> convertedEntries = null;
    if (input.getEntries() != null) {
      convertedEntries =
          input.getEntries().stream().map(this::convertEntry).collect(Collectors.toList());
    }
    HomeTimelineModel res =
        new HomeTimelineModel()
            .withId(input.getId())
            .withUserId(input.getUserId())
            .withFollowersId(input.getFollowersId())
            .withEntries(convertedEntries);
    return res;
  }

  private HomeTimelineModel.HomeTimelineEntryModel convertEntry(
      HomeTimelineEntity.HomeTimelineEntryEntity entry) {
    if (entry == null) {
      return null;
    }

    List<HomeTimelineModel.HomeTimelineLikedByModel> convertedLikedBy = null;
    if (entry.getLikedBy() != null) {
      convertedLikedBy =
          entry.getLikedBy().stream().map(this::convertLikedBy).collect(Collectors.toList());
    }

    return new HomeTimelineModel.HomeTimelineEntryModel()
        .withPostId(entry.getPostId())
        .withAuthorId(entry.getAuthorId())
        .withContent(entry.getContent())
        .withLikedBy(convertedLikedBy)
        .withType(entry.getType())
        .withTimestamp(entry.getTimestamp());
  }

  private HomeTimelineModel.HomeTimelineLikedByModel convertLikedBy(
      HomeTimelineEntity.HomeTimelineLikedByEntity entry) {
    if (entry == null) {
      return null;
    }

    return new HomeTimelineModel.HomeTimelineLikedByModel()
        .withUserId(entry.getUserId())
        .withLikedAt(entry.getLikedAt());
  }
}
