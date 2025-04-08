package com.epita.exchange.s3.service;

import com.epita.exchange.utils.Logger;
import io.minio.*;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.io.File;
import java.io.InputStream;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.security.InvalidParameterException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ApplicationScoped
public class S3Service implements Logger {
  // private MinioClient minioClient;
  private List<MinioClient> minioClientList;

  @Inject
  @ConfigProperty(name = "s3.endpoints", defaultValue = "http://localhost:9000")
  List<String> endpoints;

  @Inject
  @ConfigProperty(name = "s3.accessKeys", defaultValue = "minioadmin")
  List<String> accessKeys;

  @Inject
  @ConfigProperty(name = "s3.secretKeys", defaultValue = "minioadmin")
  List<String> secretKeys;

  @Inject
  @ConfigProperty(name = "s3.bucketName", defaultValue = "default")
  String bucketName;

  @Inject
  @ConfigProperty(name = "s3.minio_number", defaultValue = "1")
  int minio_number;

  @PostConstruct
  public void init() {
    if (secretKeys.size() != minio_number || accessKeys.size() != minio_number || endpoints.size() != minio_number)
      throw new RuntimeException("Minio instantiation failed: size of list do not match");
    minioClientList = new ArrayList<>();
    for (int i = 0; i < minio_number; i++)
    {
      minioClientList.add(MinioClient.builder().endpoint(endpoints.get(i)).credentials(accessKeys.get(i), secretKeys.get(i)).build());
      logger().info("S3Configuration {} - Endpoint: {}", i, endpoints.get(i));
      logger().info("S3Configuration {} - Bucket: {}", i, bucketName);
      logger().info("S3Configuration {} - AccessKey: {}", i, accessKeys.get(i));
    }
    ensureBucketExists();
  }

  public void ensureBucketExists() {
    try {
      for (int i = 0; i < minio_number; i++)
      {
        logger().info("Verification if bucket {} exist for minio-{}", bucketName, i);
        boolean found =
                minioClientList.get(i).bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if (!found) {
          minioClientList.get(i).makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
          logger().info("Bucket created: {}", bucketName);
        } else {
          logger().info("Bucket already exists: {}", bucketName);
        }
      }
    } catch (Exception e) {
      logger().error("Failed to verify or create bucket: {}", e.getMessage());
      throw new RuntimeException("Bucket verification/creation failed", e);
    }
  }

  public void uploadFile(String key, InputStream inputStream, long size) {
    try {
      int n = ThreadLocalRandom.current().nextInt(0, minio_number);
      minioClientList.get(n).putObject(
          PutObjectArgs.builder().bucket(bucketName).object(key).stream(inputStream, size, -1)
              .contentType("application/octet-stream")
              .build());
      logger().info("File uploaded : {} in minio {}", key, n);
    } catch (Exception e) {
      logger().error("Failed to upload file to MinIO");
      throw new RuntimeException("Failed to upload file to MinIO", e);
    }
  }

  private Map.Entry<Integer, String> extractMinioData(String input) {
    Pattern pattern = Pattern.compile("minio-(\\d+)/(user/[^/]+/image/.+)");
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
      Map.Entry<Integer, String> values = this.extractMinioData(key);
      if (values.getKey() < 0 || values.getKey() >= minio_number)
        throw new InvalidParameterException("");
      minioClientList.get(values.getKey()).downloadObject(
          DownloadObjectArgs.builder()
              .bucket(bucketName)
              .object(values.getValue())
              .filename(downloadPath)
              .build());
      logger().info("File downloaded: {}", values.getValue());
      return new File(downloadPath);
    } catch (Exception e) {
      logger().error("Failed to download file from MinIO");
      throw new RuntimeException("Failed to download file from MinIO", e);
    }
  }

  public void deleteFile(String key) {
    try {
      Map.Entry<Integer, String> values = this.extractMinioData(key);
      if (values.getKey() < 0 || values.getKey() >= minio_number)
        throw new InvalidParameterException("");
      minioClientList.get(values.getKey()).removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(values.getValue()).build());
      logger().info("File deleted: {}", values.getValue());
    } catch (Exception e) {
      logger().error("Failed to delete file from MinIO");
      throw new RuntimeException("Failed to delete file from MinIO", e);
    }
  }
}
