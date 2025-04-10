package com.epita.srvc_media;

import com.epita.exchange.utils.Logger;
import jakarta.ws.rs.*;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import java.io.InputStream;
import java.net.URLConnection;

@Provider
public class SrvcMediaFilter implements ContainerRequestFilter, Logger {

  @Override
  public void filter(ContainerRequestContext requestContext) throws IOException {
    String path = requestContext.getUriInfo().getPath();

    if (path.startsWith("minio/")) {
      String id = path.substring("minio/".length());

      logger.info("GET /minio/{}", id);

      try {
        requestContext.abortWith(handleMinioRequest(id));
      } catch (Exception e) {
        logger.error("Error processing Minio request", e);
        requestContext.abortWith(Response.serverError().entity("Internal server error").build());
      }
    }
  }

  private Response handleMinioRequest(String id) {
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
      logger.error("Failed to read stream or detect MIME type", e);
      return Response.serverError().entity("Error while reading the file").build();
    }
  }
}
