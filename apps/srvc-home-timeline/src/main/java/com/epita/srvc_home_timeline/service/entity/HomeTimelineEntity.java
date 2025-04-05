package com.epita.srvc_home_timeline.service.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import lombok.*;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
@With
public class HomeTimelineEntity implements Serializable {
  private String Id;
  private String userId;
  private List<HomeTimelineEntryEntity> entries;
  private List<String> followersId;

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
