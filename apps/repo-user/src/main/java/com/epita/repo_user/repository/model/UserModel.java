package com.epita.repo_user.repository.model;

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
@MongoEntity(collection = "UserModel", database = "RepoUser")
public class UserModel {
  @BsonId private ObjectId id;
  private String username;
  private String email;
  private String passwordHash;
  private String bio;
  private String profileImage;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private boolean deleted = false;
}
