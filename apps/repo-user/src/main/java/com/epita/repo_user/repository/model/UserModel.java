package com.epita.repo_user.repository.model;

import io.quarkus.mongodb.panache.common.MongoEntity;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonId;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@MongoEntity(collection = "users", database = "")
public class UserModel {
  @BsonId private String id;
  private String username;
  private String email;
  private String passwordHash;
  private String bio;
  private String profileImage;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private boolean deleted = false;
}
