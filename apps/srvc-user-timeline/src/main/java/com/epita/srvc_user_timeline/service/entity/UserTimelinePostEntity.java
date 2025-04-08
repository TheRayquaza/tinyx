package com.epita.srvc_user_timeline.service.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import lombok.*;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@With
public class UserTimelinePostEntity implements Serializable {
  @BsonId private ObjectId id;
  // we add this field, compared to post model in repo-post, to store the user id to which this post
  // belongs
  private String postId;
  private String userId;
  private String ownerId;
  private String text;
  private String media;
  private String repostId;
  private String replyToPostId;
  private Boolean isReply;
  // we add this field to store the time when the post was liked for final sorting
  private LocalDateTime likedAt;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private boolean deleted;
}
