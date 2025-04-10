package com.epita.exchange.s3.service;

import com.epita.exchange.utils.Logger;
import io.minio.*;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import java.io.InputStream;

@ApplicationScoped
public class S3Service implements Logger {
  private MinioClient minioClient;

  String endpoint;

  String accessKey;

  String secretKey;

  String bucketName;

  public static String getEnv(String key, String defaultValue) {
    String value = System.getenv(key);
    return value != null ? value : defaultValue;
  }

  @PostConstruct
  public void init() {
    endpoint = getEnv("S3_ENDPOINT", "http://localhost:9000");
    accessKey = getEnv("S3_ACCESS_KEY", "minioadmin");
    secretKey = getEnv("S3_SECRET_KEY", "minioadmin");
    bucketName = getEnv("S3_BUCKET", "default");

    minioClient =
        MinioClient.builder().endpoint(endpoint).credentials(accessKey, secretKey).build();
    logger().info("S3Configuration - Endpoint: {}", endpoint);
    logger().info("S3Configuration - Bucket: {}", bucketName);
    logger().info("S3Configuration - AccessKey: {}", accessKey);
    ensureBucketExists();
  }

  public void ensureBucketExists() {
    try {
      logger().info("Verification if bucket {} exist", bucketName);
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

  public String uploadFile(String key, InputStream inputStream, long size) {
    try {
      minioClient.putObject(
          PutObjectArgs.builder().bucket(bucketName).object(key).stream(inputStream, size, -1)
              .contentType("application/octet-stream")
              .build());
      logger().info("File uploaded : {}", key);
      return "/minio/" + key;
    } catch (Exception e) {
      logger().error("Failed to upload file to MinIO");
      throw new RuntimeException("Failed to upload file to MinIO", e);
    }
  }

  public InputStream downloadFileAsStream(String key) {
    try {
      return minioClient.getObject(GetObjectArgs.builder().bucket(bucketName).object(key).build());
    } catch (Exception e) {
      logger().error("Failed to get file stream from MinIO", e);
      return null;
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
