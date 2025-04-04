package com.epita.repo_post.controller.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@AllArgsConstructor
@With
@NoArgsConstructor
@Getter
@Setter
public class ReplyPostRequest {
  public String text;
  public String media;
}
