package com.epita.repo_post.controller;

import com.epita.repo_post.controller.request.CreatePostRequest;
import com.epita.repo_post.service.entity.PostEntity;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

@Path("/")
public interface RepoPostControllerApi {

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Operation(
      summary = "Create a new post")
  @APIResponses({
          @APIResponse(responseCode = "200", description = "Post created successfully", content = @Content(schema = @Schema(implementation = PostEntity.class))),
            @APIResponse(responseCode = "400", description = "Invalid input"),
            @APIResponse(responseCode = "401", description = "User not authorized"),
            @APIResponse(responseCode = "404", description = "User Not Found"),
  })
  @NotNull
  PostEntity createPost(@RequestBody(required = true) @NotNull @Valid CreatePostRequest request);
}
