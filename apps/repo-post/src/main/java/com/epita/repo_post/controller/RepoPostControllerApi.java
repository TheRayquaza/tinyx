package com.epita.repo_post.controller;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

@Path("/hello")
public interface RepoPostControllerApi {

  @GET
  @Produces(MediaType.TEXT_PLAIN)
  @Operation(
      summary = "Hello Endpoint",
      description = "Returns a simple hello message from RESTEasy Reactive")
  @APIResponse(responseCode = "200", description = "Successfully returns a hello message")
  String hello();
}
