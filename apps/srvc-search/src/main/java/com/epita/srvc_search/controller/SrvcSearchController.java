package com.epita.srvc_search.controller;

import com.epita.exchange.auth.service.AuthService;
import com.epita.srvc_search.service.SearchService;
import com.epita.srvc_search.service.entity.SearchEntity;
import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

import java.util.List;

public class SrvcSearchController implements SrvcSearchControllerApi {

    @Inject
    SearchService searchService;

    @Inject
    AuthService authService;

    @POST
    @Path("/search")
    @Authenticated
    @Override
    public List<SearchEntity> searchPosts(@RequestBody(required = true) @NotNull @Valid SearchEntity request) {
        String userId = authService.getUserId();
        logger().info("POST /search - User {} search for posts", userId);
        return searchService.searchPosts(request);
    }
}
