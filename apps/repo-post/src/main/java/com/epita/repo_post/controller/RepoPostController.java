package com.epita.repo_post.controller;

import com.epita.exchange.auth.service.AuthService;
import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/hello")
public class RepoPostController implements RepoPostControllerApi {

  @Inject AuthService authService;

  @Authenticated
  @GET
  public String hello() {
    authService.getUserId();
    return "Hello from RESTEasy Reactive";
  }
}
