package com.epita.srvc_home_timeline.service.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

import lombok.*;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
@With
public class HomeTimelineEntity implements Serializable {
  @BsonId private ObjectId id;
  private String userId;
  private List<HomeTimelineEntryEntity> entries;
  private List<String> followersId;
  private List<String> blockedUsersId;

  @Getter
  @Setter
  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  @With
  public static class HomeTimelineEntryEntity implements Serializable {
    private String postId;
    private String authorId;
    private String content;
    private List<HomeTimelineLikedByEntity> likedBy;
    private String type;
    private LocalDateTime timestamp;
  }

  @Getter
  @Setter
  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  @With
  public static class HomeTimelineLikedByEntity implements Serializable {
    private String userId;
    private LocalDateTime likedAt;
  }
}
