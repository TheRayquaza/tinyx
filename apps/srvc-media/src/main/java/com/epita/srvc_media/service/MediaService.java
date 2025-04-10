package com.epita.srvc_media.service;

import com.epita.exchange.s3.service.S3Service;
import com.epita.exchange.utils.Logger;
import com.epita.srvc_media.SrvcMediaErrorCode;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.io.*;

@ApplicationScoped
public class MediaService implements Logger {

  @Inject S3Service s3Service;

  private static final String tmpDir = "/tmp";

  public InputStream downloadMedia(String mediaId) {
    if (mediaId == null || mediaId.isBlank()) {
      throw SrvcMediaErrorCode.INVALID_MEDIA_ID.createError("mediaId");
    }

    try {
      String downloadMediaPath = tmpDir + "/" + mediaId;
      logger().info("Downloading media from S3: {}", downloadMediaPath);
      s3Service.downloadFile(mediaId, downloadMediaPath);

      File file = new File(downloadMediaPath);
      if (!file.exists()) {
        logger().error("File not found: {}", downloadMediaPath);
        return null;
      }

      return new FileInputStream(file);

    } catch (IOException e) {
      logger().error("Failed to download media: {}", e.getMessage());
      throw SrvcMediaErrorCode.MEDIA_DOWNLOAD_FAILED.createError(e.getMessage());
    }
  }
}
