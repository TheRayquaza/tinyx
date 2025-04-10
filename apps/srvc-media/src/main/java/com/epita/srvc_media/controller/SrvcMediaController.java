package com.epita.srvc_media.controller;

import com.epita.srvc_media.service.MediaService;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.InputStream;
import java.net.URLConnection;

@Produces(MediaType.APPLICATION_OCTET_STREAM)
@Path("/minio")
public class SrvcMediaController implements SrvcMediaControllerApi {

  @Inject MediaService mediaService;

  @GET
  @Path("/{id: .+}}")
  @Override
  public @NotNull Response downloadObject(@PathParam("id") @NotNull String id) {
    logger().info("GET /minio/{}", id);

    InputStream stream = mediaService.downloadMedia(id);
    if (stream == null) {
      return Response.status(Response.Status.NOT_FOUND).entity("File not found").build();
    }

    try {
      if (stream.markSupported()) {
        stream.mark(20);
      }

      String mimeType = URLConnection.guessContentTypeFromStream(stream);
      if (mimeType == null) {
        mimeType = MediaType.APPLICATION_OCTET_STREAM;
      }

      if (stream.markSupported()) {
        stream.reset();
      }

      String fileName = id;

      return Response.ok(stream, mimeType)
          .header("Content-Disposition", "inline; filename=\"" + fileName + "\"")
          .build();

    } catch (Exception e) {
      logger().error("Failed to read stream or detect MIME type", e);
      return Response.serverError().entity("Error while reading the file").build();
    }
  }
}
