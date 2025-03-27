package com.epita.repo_post.controller.response;

import lombok.*;
import java.util.List;

import com.epita.repo_post.service.entity.PostEntity;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AllRepliesResponse {
    private List<PostEntity> replies;
}
