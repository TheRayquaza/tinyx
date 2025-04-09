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
        // Upload an object
        s3Service.uploadFile(objectKey, request.getFile(), request.getFile().available()); // TODO: fix size
        // Delete an object
        s3Service.deleteFile(userModel.getProfileImage());
        // Download an object
        File file = s3Service.downloadFile(key, path);
  }
```

## Debug

Use minio console accessible via http://localhost:9000
