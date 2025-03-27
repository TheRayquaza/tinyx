package com.epita.repo_post.controller.request;

import java.util.List;
import lombok.*;

@AllArgsConstructor
@With
@NoArgsConstructor
@Getter
@Setter
public class EditPostRequest {
  // Either text or media should be present
  public String text;
  public List<String> media;
}
