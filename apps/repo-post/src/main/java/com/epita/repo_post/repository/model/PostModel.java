package com.epita.repo_post.repository.model;

import io.quarkus.mongodb.panache.common.MongoEntity;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@MongoEntity(collection = "PostModel", database = "RepoPost")
public class PostModel {
  @BsonId private ObjectId id;
  private String ownerId;
  private String text;
  private String media;
  private String repostId;
  private String replyToPostId;
  private Boolean isReply;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private boolean deleted = false;
}
