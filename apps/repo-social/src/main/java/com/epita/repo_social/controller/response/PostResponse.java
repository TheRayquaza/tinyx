package com.epita.repo_social.controller.response;

import java.time.LocalDateTime;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@With
public class PostResponse {
  private String id;
  private String ownerId;
  private String text;
  private String media;
  private String repostId;
  private String replyToPostId;
  private Boolean isReply;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private boolean deleted;
}
