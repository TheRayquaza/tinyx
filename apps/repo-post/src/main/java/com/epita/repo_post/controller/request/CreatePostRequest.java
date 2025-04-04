package com.epita.repo_post.controller.request;

import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.core.MediaType;
import java.io.InputStream;
import lombok.*;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jboss.resteasy.reactive.PartType;

@AllArgsConstructor
@With
@NoArgsConstructor
@Getter
@Setter
public class CreatePostRequest {
  @FormParam("json")
  @PartType(MediaType.APPLICATION_JSON)
  public String text;

  @FormParam("extension")
  @PartType(MediaType.APPLICATION_JSON)
  public String extension;

  @FormParam("media")
  @PartType(MediaType.APPLICATION_OCTET_STREAM)
  @Schema(description = "Media input stream")
  public InputStream media;
}
