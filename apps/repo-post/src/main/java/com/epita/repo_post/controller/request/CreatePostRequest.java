package com.epita.repo_post.controller.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@AllArgsConstructor
@With
@NoArgsConstructor
@Getter
@Setter
public class CreatePostRequest {
    @NotNull
    public String ownerId;
    // Either text or media should be present
    public String text;
    public String media;
}
