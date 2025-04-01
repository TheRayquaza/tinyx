package com.epita.repo_social.controller;

import com.epita.exchange.auth.service.AuthService;
import com.epita.repo_social.service.SocialService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("/social")
public class RepoSocialController implements RepoSocialControllerApi {

  @Inject AuthService authService;

  @Inject SocialService socialService;

  @POST
  @Path("/post/{postId}/like")
  @Override
  public void likePost(String postId) {
    logger().info("POST /social/post/{}/like", postId);
    socialService.likePost(postId);
  }
}
