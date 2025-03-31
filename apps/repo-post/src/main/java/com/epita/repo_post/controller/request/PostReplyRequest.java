package com.epita.repo_post.controller.request;

import lombok.*;

@AllArgsConstructor
@With
@NoArgsConstructor
@Getter
@Setter
public class PostReplyRequest {
  String text;
  String media;
}
