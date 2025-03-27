package com.epita.repo_post.controller.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@AllArgsConstructor
@With
@NoArgsConstructor
@Getter
@Setter
public class ReplyPostRequest {
    @NotNull public String ownerId;
    @NotNull public String text;
}
