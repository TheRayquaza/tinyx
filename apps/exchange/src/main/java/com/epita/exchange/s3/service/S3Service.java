package com.epita.exchange.s3.service;

import com.epita.exchange.utils.Logger;
import io.minio.*;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.io.File;
import java.io.InputStream;
import java.util.AbstractMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.microprofile.config.inject.ConfigProperty;

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
      return key;
    } catch (Exception e) {
      logger().error("Failed to upload file to MinIO");
      throw new RuntimeException("Failed to upload file to MinIO", e);
    }
  }

  private Map.Entry<Integer, String> extractMinioData(String input) {
    Pattern pattern = Pattern.compile("minio-(\\d+)/(.*)");
    Matcher matcher = pattern.matcher(input);

    if (matcher.matches()) {
      int minioNumber = Integer.parseInt(matcher.group(1));
      String path = matcher.group(2);
      return new AbstractMap.SimpleEntry<>(minioNumber, path);
    } else {
      throw new IllegalArgumentException("Input does not match expected pattern: " + input);
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
