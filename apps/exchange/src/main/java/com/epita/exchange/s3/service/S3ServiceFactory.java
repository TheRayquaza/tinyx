package com.epita.exchange.s3.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class S3ServiceFactory {
    @Inject
    @ConfigProperty(name = "s3.endpoint")
    String defaultEndpoint;

    @Inject
    @ConfigProperty(name = "s3.bucket")
    String defaultBucketName;

    @Inject
    @ConfigProperty(name = "s3.access-key")
    String defaultAccessKey;

    @Inject
    @ConfigProperty(name = "s3.secret-key")
    String defaultSecretKey;

    @Produces
    public S3Service createDefaultS3Service() {
        S3Configuration config = new S3Configuration(defaultEndpoint, defaultBucketName, defaultAccessKey, defaultSecretKey);
        return new S3Service(config);
    }

    public S3Service createCustomS3Service(String endpoint, String bucketName, String accessKey, String secretKey) {
        S3Configuration config = new S3Configuration(endpoint, bucketName, accessKey, secretKey);
        return new S3Service(config);
    }
}
