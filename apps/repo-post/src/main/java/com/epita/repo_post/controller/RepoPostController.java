package com.epita.repo_post.controller;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/hello")
public class RepoPostController implements RepoPostControllerApi {

    @GET
    public String hello() {
        return "Hello from RESTEasy Reactive";
    }
}
