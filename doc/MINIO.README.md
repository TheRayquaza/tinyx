# Minio

## What is Minio used for ?

Minio is an implementation of the S3 protocol which is very efficient for storing media / file like images, videos ...

## S3Service

A S3Service is available :

```java
@ApplicationScoped
public class UserService implements Logger {

  @Inject S3Service s3Service;

...
  public void MyFunction() {
         s3Service.uploadFile(objectKey, request.getFile(), request.getFile().available()); // TODO: fix size
  }
```

## Debug
