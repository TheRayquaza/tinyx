package com.epita.srvc_home_timeline.repository.model;

import io.quarkus.mongodb.panache.common.MongoEntity;
import java.time.LocalDateTime;
import lombok.*;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@With
@MongoEntity(collection = "HomeTimelineModel", database = "SrvcHomeTimeline")
public class HomeTimelinePostModel {
  @BsonId private ObjectId id;
  private String postId;
  private String ownerId;
  private String text;
  private String media;
  private String repostId;
  private String replyToPostId;
  private boolean isReply;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private boolean deleted;
}
