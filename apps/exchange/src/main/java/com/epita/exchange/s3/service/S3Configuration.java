package com.epita.exchange.s3.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class S3Configuration {

  @ConfigProperty(name = "s3.endpoint")
  @Inject
  public String endpoint;

  @ConfigProperty(name = "s3.accessKey")
  @Inject
  public String accessKey;

  @ConfigProperty(name = "s3.secretKey")
  @Inject
  public String secretKey;

  @ConfigProperty(name = "s3.bucketName")
  @Inject
  public String bucketName;
}
