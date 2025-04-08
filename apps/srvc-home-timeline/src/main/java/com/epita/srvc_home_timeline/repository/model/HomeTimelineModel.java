package com.epita.srvc_home_timeline.repository.model;

import io.quarkus.mongodb.panache.common.MongoEntity;
import java.time.LocalDateTime;
import java.util.List;
import lombok.*;
import org.bson.codecs.pojo.annotations.BsonId;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@MongoEntity(collection = "HomeTimelineModel", database = "SrvcHomeTimeline")
@With
public class HomeTimelineModel {
  @BsonId private String id;
  private String userId;
  private LocalDateTime createdAt;
  private List<HomeTimelineEntryModel> entries;
  private List<String> followersId;

  @Getter
  @Setter
  @AllArgsConstructor
  @NoArgsConstructor
  @With
  public static class HomeTimelineEntryModel {
    private String postId;
    private String authorId;
    private String content;
    private List<HomeTimelineLikedByModel> likedBy;
    private String type;
    private LocalDateTime timestamp;
  }

  @Getter
  @Setter
  @AllArgsConstructor
  @NoArgsConstructor
  @With
  public static class HomeTimelineLikedByModel {
    private String userId;
    private LocalDateTime likedAt;
  }
}
