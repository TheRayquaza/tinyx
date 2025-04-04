package com.epita.repo_post.controller.request;

import lombok.*;
import jakarta.ws.rs.FormParam;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jboss.resteasy.reactive.PartType;

import java.io.InputStream;

@AllArgsConstructor
@With
@NoArgsConstructor
@Getter
@Setter
public class ReplyPostRequest {
  public String text;

  @FormParam("media")
  @PartType("image/jpeg")
  @Schema(description = "Media image associated to the post")
  public InputStream media;
}
