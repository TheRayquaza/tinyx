package com.epita.repo_post.controller.response;

import com.epita.repo_post.service.entity.PostEntity;
import java.util.List;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@With
public class AllRepliesResponse {
  private List<PostEntity> replies;
}
