package com.epita.repo_post.controller.request;

import lombok.*;

@AllArgsConstructor
@With
@NoArgsConstructor
@Getter
@Setter
public class PostReplyRequest {
  public String text;
  public String media;
}
