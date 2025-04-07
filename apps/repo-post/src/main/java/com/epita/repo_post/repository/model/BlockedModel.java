package com.epita.repo_post.repository.model;

import io.quarkus.mongodb.panache.common.MongoEntity;
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
@MongoEntity(collection = "BlockedModel", database = "RepoPost")
public class BlockedModel {
  @BsonId private ObjectId id;
  private String userId;
  private String targetId;
  private boolean blocked;
}
