package com.epita.srvc_search.controller;

import com.epita.exchange.utils.Logger;
import com.epita.srvc_search.service.entity.SearchEntity;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.media.Content;

import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/")
public interface SrvcSearchControllerApi extends Logger {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Search for posts")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Posts found",
                    content = @Content(schema = @Schema(implementation = SearchEntity.class))),
            @APIResponse(responseCode = "400", description = "Invalid input"),
            @APIResponse(responseCode = "500", description = "Internal server error"),
    })
    List<SearchEntity> searchPosts(SearchEntity request);
}
