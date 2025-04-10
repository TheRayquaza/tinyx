package com.epita.srvc_user_timeline.controller.contract;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.*;
import org.bson.codecs.pojo.annotations.BsonId;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@With
public class UserTimelinePostResponse implements Serializable {
  @BsonId private String id;
  private String postId; // we add this field to store the post id to which this post belongs
  private String
      userId; // we add this field, compared to post model in repo-post, to store the user id to
  // which this post belongs
  private String ownerId;
  private String text;
  private String media;
  private String repostId;
  private String replyToPostId;
  private Boolean isReply;
  private LocalDateTime
      likedAt; // we add this field to store the time when the post was liked for final sorting
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private boolean deleted = false;
}
