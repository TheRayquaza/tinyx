package com.epita.srvc_search.controller;

import com.epita.exchange.auth.service.AuthService;
import com.epita.srvc_search.controller.request.SearchRequest;
import com.epita.srvc_search.service.SearchService;
import com.epita.srvc_search.service.entity.SearchEntity;
import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/")
public class SrvcSearchController implements SrvcSearchControllerApi {

  @Inject SearchService searchService;

  @Inject AuthService authService;

  @GET
  @Path("/search")
  @Override
  public List<SearchEntity> searchPosts(
      @RequestBody(required = true) @NotNull @Valid SearchRequest request) {
    String userId = authService.getUserId();
    logger().info("POST /search - User {} search for posts", userId);
    return searchService.searchPosts(userId, request);
  }
}
