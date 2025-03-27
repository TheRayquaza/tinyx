package com.epita.repo_post.repository.model;

import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonId;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@MongoEntity(collection = "posts")
public class PostModel {
    @BsonId private String id;
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
