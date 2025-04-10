package com.epita.srvc_media.controller;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@Produces(MediaType.APPLICATION_OCTET_STREAM)
@Path("/minio")
public class SrvcMediaController{

  @GET
  @Path("{dummy: .*}")
  public void noop() {
    // This method will never run — the filter will intercept first
  }

}