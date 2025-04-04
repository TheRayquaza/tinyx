package com.epita.repo_post.controller.request;

import jakarta.ws.rs.FormParam;
import lombok.*;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jboss.resteasy.reactive.PartType;

import java.io.InputStream;

@AllArgsConstructor
@With
@NoArgsConstructor
@Getter
@Setter
public class EditPostRequest {
  // Either text or media should be present
  public String text;

  @FormParam("media")
  @PartType("image/jpeg")
  @Schema(description = "Media image associated to the post")
  public InputStream media;
}
