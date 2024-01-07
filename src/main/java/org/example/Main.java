package org.example;


import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.List;


public class Main {
    static String ACCESS_KEY = "AKIAU7AKZSYL6VKE6CAW";
    static String SECRET_ACCESS_KEY = "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";

    static String S3_FILE_NAME = "TestFolder/file.txt";

    public static void main(String[] args) {
        System.out.println("Hello world!");

        String bucketName = "jdbctestbucket"; // Replace with your bucket name

        S3Client s3Client = S3Client.builder()
                .credentialsProvider(() -> AwsBasicCredentials.create(ACCESS_KEY, SECRET_ACCESS_KEY))
                .build();


        ListObjectsRequest request = ListObjectsRequest.builder().bucket(bucketName).build();
        ListObjectsResponse response = s3Client.listObjects(request);

        List<S3Object> objectList = response.contents();
        for (S3Object object : objectList) {
            System.out.println(object.key()+"  "+object.size());
            createPresignedGetUrl(bucketName, object.key());
        }
    }

    /* Create a pre-signed URL to download an object in a subsequent GET request. */
    public static String createPresignedGetUrl(String bucketName, String keyName) {
        try (S3Presigner presigner = S3Presigner.create()) {

            GetObjectRequest objectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(keyName)
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(600))  // The URL will expire in 10 hours.
                    .getObjectRequest(objectRequest)
                    .build();

            PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(presignRequest);
            System.out.println("Presigned URL for " + presignedRequest.httpRequest().method() + " : " + presignedRequest.url().toString());

            return presignedRequest.url().toExternalForm();
        }
    }

    public static String createPresignedPutUrl(String bucketName, String objectKey) {
        try (S3Presigner presigner = S3Presigner.create()) {

            PutObjectRequest objectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .build();

            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(600))  // The URL will expire in 10 hours.
                    .putObjectRequest(objectRequest)
                    .build();

            PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(presignRequest);
            System.out.println("Presigned URL for " + presignedRequest.httpRequest().method() + " : " + presignedRequest.url().toString());

            return presignedRequest.url().toExternalForm();
        }
    }

}
