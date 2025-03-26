package com.epita.exchange.s3.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class S3Configuration {
  @ConfigProperty(defaultValue = "s3.endpoint")
  public String endpoint;

  @ConfigProperty(name = "s3.bucket")
  public String bucketName;

  @ConfigProperty(name = "s3.access-key")
  public String accessKey;

  @ConfigProperty(name = "s3.secret-key")
  public String secretKey;
}
