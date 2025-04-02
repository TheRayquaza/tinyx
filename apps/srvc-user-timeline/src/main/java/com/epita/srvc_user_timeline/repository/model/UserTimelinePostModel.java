package com.epita.srvc_user_timeline.repository.model;

import io.quarkus.mongodb.panache.common.MongoEntity;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonId;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@MongoEntity(collection = "UserTimelinePost", database = "SrvcUserTimeline")
public class UserTimelinePostModel {
  @BsonId private String id;
  private String userId;
  private String ownerId;
  private String text;
  private List<String> media;
  private String repostId;
  private String replyToPostId;
  private Boolean isReply;
  private LocalDateTime likedAt;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private boolean deleted;
}
