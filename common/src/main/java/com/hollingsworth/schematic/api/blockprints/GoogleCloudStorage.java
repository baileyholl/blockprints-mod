package com.hollingsworth.schematic.api.blockprints;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;

public class GoogleCloudStorage {
    public static boolean uploadFileToGCS(URL signedUrl, Path filePath, String contentType, int fileSize) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(signedUrl.toString()))
                .header("Content-Type", contentType)
                .header("x-goog-content-length-range", "0," + fileSize)
                .PUT(HttpRequest.BodyPublishers.ofFile(filePath))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if(response.statusCode() != 200){
            System.out.println(response.body());
        }
        return response.statusCode() == 200;
    }
}
