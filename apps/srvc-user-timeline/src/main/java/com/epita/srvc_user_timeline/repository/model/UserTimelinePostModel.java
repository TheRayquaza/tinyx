package com.epita.srvc_user_timeline.repository.model;

import io.quarkus.mongodb.panache.common.MongoEntity;
import java.time.LocalDateTime;
import java.util.List;

import lombok.*;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

@Getter
@Setter
@With
@AllArgsConstructor
@NoArgsConstructor
@MongoEntity(collection = "UserTimelinePost", database = "SrvcUserTimeline")
public class UserTimelinePostModel {
  @BsonId private ObjectId id;
  private String postId;
  private String userId;
  private String ownerId;
  private String text;
  private String media;
  private String repostId;
  private String replyToPostId;
  private Boolean isReply;
  private LocalDateTime likedAt;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private boolean deleted;
}
