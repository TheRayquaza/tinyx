package com.epita.exchange.s3.service;

import com.epita.exchange.utils.Logger;
import io.minio.*;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.io.File;
import java.io.InputStream;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import java.util.concurrent.ThreadLocalRandom;

@ApplicationScoped
public class S3Service implements Logger {
  private MinioClient minioClient;

  @Inject
  @ConfigProperty(name = "s3.endpoint", defaultValue = "http://localhost:9000")
  String endpoint;

  @Inject
  @ConfigProperty(name = "s3.accessKey", defaultValue = "minioadmin")
  String accessKey;

  @Inject
  @ConfigProperty(name = "s3.secretKey", defaultValue = "minioadmin")
  String secretKey;

  @Inject
  @ConfigProperty(name = "s3.bucketName", defaultValue = "default")
  String bucketName;

  @PostConstruct
  public void init() {
    minioClient =
        MinioClient.builder().endpoint(endpoint).credentials(accessKey, secretKey).build();

    logger().info("S3Configuration - Endpoint: {}", endpoint);
    logger().info("S3Configuration - Bucket: {}", bucketName);
    logger().info("S3Configuration - AccessKey: {}", accessKey);
    logger().info("S3Configuration - Bucket: {}", bucketName);

    ensureBucketExists();
  }

  public void ensureBucketExists() {
    try {
      boolean found =
          minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
      if (!found) {
        minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        logger().info("Bucket created: {}", bucketName);
      } else {
        logger().info("Bucket already exists: {}", bucketName);
      }
    } catch (Exception e) {
      logger().error("Failed to verify or create bucket: {}", e.getMessage());
      throw new RuntimeException("Bucket verification/creation failed", e);
    }
  }

  public void uploadFile(String key, InputStream inputStream, long size) {
    try {
      int n = ThreadLocalRandom.current().nextInt(0, 5);
      String prefixedKey = "minio-" + n + "/" + key;
      minioClient.putObject(
          PutObjectArgs.builder().bucket(bucketName).object(prefixedKey).stream(inputStream, size, -1)
              .contentType("application/octet-stream")
              .build());
      logger().info("File uploaded : {}", prefixedKey);
    } catch (Exception e) {
      logger().error("Failed to upload file to MinIO");
      throw new RuntimeException("Failed to upload file to MinIO", e);
    }
  }

  public File downloadFile(String key, String downloadPath) {
    try {
      minioClient.downloadObject(
          DownloadObjectArgs.builder()
              .bucket(bucketName)
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

  public void deleteFile(String key) {
    try {
      minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(key).build());
      logger().info("File deleted: {}", key);
    } catch (Exception e) {
      logger().error("Failed to delete file from MinIO");
      throw new RuntimeException("Failed to delete file from MinIO", e);
    }
  }
}
