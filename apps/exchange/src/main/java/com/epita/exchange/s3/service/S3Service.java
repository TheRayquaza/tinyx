package com.epita.exchange.s3.service;

import com.epita.exchange.utils.Logger;
import io.minio.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.constraints.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

@ApplicationScoped
public class S3Service implements Logger {

  public void uploadFile(@NotNull S3Configuration s3Configuration, String key, File file) {
    MinioClient minioClient =
        MinioClient.builder()
            .endpoint(s3Configuration.endpoint)
            .credentials(s3Configuration.accessKey, s3Configuration.secretKey)
            .build();

    try (InputStream inputStream = new FileInputStream(file)) {
      minioClient.putObject(
          PutObjectArgs.builder().bucket(s3Configuration.bucketName).object(key).stream(
                  inputStream, file.length(), -1)
              .contentType("application/octet-stream")
              .build());
      logger().info("File uploaded: {}", key);
    } catch (Exception e) {
      logger().error("Failed to upload file to MinIO");
      throw new RuntimeException("Failed to upload file to MinIO", e);
    }
  }

  public File downloadFile(@NotNull S3Configuration s3Configuration, String key, String downloadPath) {
    MinioClient minioClient =
        MinioClient.builder()
            .endpoint(s3Configuration.endpoint)
            .credentials(s3Configuration.accessKey, s3Configuration.secretKey)
            .build();

    try {
      minioClient.downloadObject(
          DownloadObjectArgs.builder()
              .bucket(s3Configuration.bucketName)
              .object(key)
              .filename(downloadPath)
              .build());
      logger().info("File downloaded: {}", key);
      return new File(downloadPath);
    } catch (Exception e) {
      logger().error("Failed to download file from MinIO");
      throw new RuntimeException("Failed to download file from MinIO", e);
    }
  }

  public void deleteFile(@NotNull S3Configuration s3Configuration, String key) {
    MinioClient minioClient =
        MinioClient.builder()
            .endpoint(s3Configuration.endpoint)
            .credentials(s3Configuration.accessKey, s3Configuration.secretKey)
            .build();

    try {
      minioClient.removeObject(
          RemoveObjectArgs.builder().bucket(s3Configuration.bucketName).object(key).build());
      logger().info("File deleted: {}", key);
    } catch (Exception e) {
      logger().error("Failed to delete file from MinIO");
      throw new RuntimeException("Failed to delete file from MinIO", e);
    }
  }
}
