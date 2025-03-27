package com.epita.exchange.s3.service;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.quarkus.runtime.annotations.ConfigItem;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
@ConfigGroup
public class S3Configuration {

  @ConfigItem(name = "s3.endpoint")
  public String endpoint;

  @ConfigItem(name = "s3.accessKey")
  public String accessKey;

  @ConfigItem(name = "s3.secretKey")
  public String secretKey;

  @ConfigItem(name = "s3.bucketName")
  public String bucketName;
}
