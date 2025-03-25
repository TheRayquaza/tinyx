package com.epita.repo_user.controller.request;

import jakarta.ws.rs.FormParam;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jboss.resteasy.reactive.PartType;

import java.io.InputStream;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UploadImageRequest {
    @FormParam("userId")
    @Schema(description = "User ID", example = "123e4567-e89b-12d3-a456-426614174000")
    private String userId;

    @FormParam("file")
    @PartType("image/jpeg")
    @Schema(description = "User profile image")
    private InputStream file;
}
