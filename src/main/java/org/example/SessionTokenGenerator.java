package org.example;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.model.Credentials;
import software.amazon.awssdk.services.sts.model.GetSessionTokenRequest;
import software.amazon.awssdk.services.sts.model.GetSessionTokenResponse;

public class SessionTokenGenerator {
    public static void main(String[] args) {
        // Set your AWS access key ID and secret access key here (permanent credentials)
        String accessKeyId = Main.ACCESS_KEY;
        String secretAccessKey = Main.SECRET_ACCESS_KEY;

        StsClient stsClient = StsClient.builder()
                .credentialsProvider(() -> AwsBasicCredentials.create(accessKeyId, secretAccessKey))
                .build();

        GetSessionTokenRequest getSessionTokenRequest = GetSessionTokenRequest.builder()
                .durationSeconds(3600) // Duration of the temporary session (in seconds)
                .build();

        GetSessionTokenResponse sessionTokenResponse = stsClient.getSessionToken(getSessionTokenRequest);

        Credentials credentials = sessionTokenResponse.credentials();
        String awsAccessKeyId = credentials.accessKeyId();
        String awsSecretAccessKey = credentials.secretAccessKey();
        String awsSessionToken = credentials.sessionToken();

        System.out.println("AWS Access Key ID: " + awsAccessKeyId);
        System.out.println("AWS Secret Access Key: " + awsSecretAccessKey);
        System.out.println("AWS Session Token: " + awsSessionToken);
    }
}
