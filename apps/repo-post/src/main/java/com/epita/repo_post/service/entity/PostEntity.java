package com.epita.repo_post.service.entity;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
@With
public class PostEntity implements Serializable {
    private String id;
    private String ownerId;
    private String text;
    private String media;
    private String repostId;
    private String replyToPostId;
    private Boolean isReply;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean deleted;

}
