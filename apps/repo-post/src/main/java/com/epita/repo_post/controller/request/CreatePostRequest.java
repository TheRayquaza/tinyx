package com.epita.repo_post.controller.request;

import lombok.*;

@AllArgsConstructor
@With
@NoArgsConstructor
@Getter
@Setter
public class CreatePostRequest {
  // Either text or media should be present
  public String text;
  public String media;
}
