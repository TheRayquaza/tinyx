package com.epita.repo_post.controller;

import com.epita.exchange.auth.service.AuthService;
import com.epita.repo_post.controller.request.CreatePostRequest;
import com.epita.repo_post.service.entity.PostEntity;
import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import jakarta.ws.rs.core.MediaType;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/")
public class RepoPostController implements RepoPostControllerApi {

  @Inject AuthService authService;

  @POST
  @Path("/create")
  @Authenticated
  @Override
  public PostEntity createPost(@RequestBody(required = true) @NotNull @Valid CreatePostRequest request) {
    return null;
  }
}
