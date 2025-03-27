package com.epita.repo_post.controller.request;

import lombok.*;
import java.util.List;

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
