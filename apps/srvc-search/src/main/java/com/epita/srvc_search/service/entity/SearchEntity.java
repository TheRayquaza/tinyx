package com.epita.srvc_search.service.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import lombok.*;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
@With
public class SearchEntity implements Serializable {
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