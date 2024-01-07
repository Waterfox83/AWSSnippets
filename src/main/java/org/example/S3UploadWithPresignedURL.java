package org.example;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;

public class S3UploadWithPresignedURL {

    static String S3_FILE_NAME = "TestFolder/file.txt";
    public static void main(String[] args) {
        String bucketName = "jdbctestbucket";
        String preSignedUrlPut = Main.createPresignedPutUrl(bucketName, S3_FILE_NAME);
        String filePath1 = "/etcd.txt"; // Replace with the path to your file

        URL resourceUrl = S3UploadWithPresignedURL.class.getResource(filePath1);
        File file = new File(resourceUrl.getFile());

        useHttpClientToPut(preSignedUrlPut, file);
        String preSignedUrlGet = Main.createPresignedGetUrl(bucketName, S3_FILE_NAME);
        useHttpClientToGet(preSignedUrlGet);
    }


    /* Use the JDK HttpClient (since v11) class to do the upload. */
    public static void useHttpClientToPut(String presignedUrlString, File fileToPut) {
        System.out.println("Uploading file to S3:" + fileToPut.toString());

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();

        HttpClient httpClient = HttpClient.newHttpClient();
        try {
            final HttpResponse<Void> response = httpClient.send(requestBuilder
                            .uri(new URL(presignedUrlString).toURI())
                            .PUT(HttpRequest.BodyPublishers.ofFile(Path.of(fileToPut.toURI())))
                            .build(),
                    HttpResponse.BodyHandlers.discarding());

            System.out.println("HTTP response code is " + response.statusCode());

        } catch (URISyntaxException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void useHttpClientToGet(String fileURL) {
        System.out.println("****** Reading file from S3: ******  \n");

        try {
            URL url = new URL(fileURL);
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line); // Print each line read from the file
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

