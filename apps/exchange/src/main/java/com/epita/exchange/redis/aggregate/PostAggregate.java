package com.epita.exchange.redis.aggregate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@With
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostAggregate implements Serializable {
  private UUID uuid;
  private String id;
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
