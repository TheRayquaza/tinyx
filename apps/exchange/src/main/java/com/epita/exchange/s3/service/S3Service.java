package com.epita.exchange.s3.service;

import com.epita.exchange.utils.Logger;
import io.minio.*;
import jakarta.enterprise.context.ApplicationScoped;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

@ApplicationScoped
public class S3Service implements Logger {
    private final MinioClient minioClient;
    private final String bucketName;

    public S3Service(S3Configuration config) {
        this.minioClient = config.createClient();
        this.bucketName = config.getBucketName();
    }

    public void uploadFile(String key, File file) {
        try (InputStream inputStream = new FileInputStream(file)) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(key)
                            .stream(inputStream, file.length(), -1)
                            .contentType("application/octet-stream")
                            .build()
            );
            logger().info("File uploaded: {}", key);
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
                            .build()
            );
            logger().info("File downloaded: {}", key);
            return new File(downloadPath);
        } catch (Exception e) {
            logger().error("Failed to download file from MinIO");
            throw new RuntimeException("Failed to download file from MinIO", e);
        }
    }

    public void deleteFile(String key) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(key)
                            .build()
            );
            logger().info("File deleted: {}", key);
        } catch (Exception e) {
            logger().error("Failed to delete file from MinIO");
            throw new RuntimeException("Failed to delete file from MinIO", e);
        }
    }
}
