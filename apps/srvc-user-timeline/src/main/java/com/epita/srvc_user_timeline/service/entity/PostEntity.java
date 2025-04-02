package com.epita.repo_post.service.entity;

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
    private List<String> media;
    private String repostId;
    private String replyToPostId;
    private Boolean isReply;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean deleted;
}
