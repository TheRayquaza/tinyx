package com.epita.exchange.s3.service;

import io.minio.MinioClient;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ApplicationScoped
public class S3Configuration {
    private String endpoint;
    private String bucketName;
    private String accessKey;
    private String secretKey;

    public MinioClient createClient() {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }
}
