package com.epita.repo_user.controller;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

public class RepoUserController implements RepoUserControllerApi {

    @GET
    @Path("/hello")
    public String hello() {
        return "Hello from RESTEasy Reactive";
    }
}
