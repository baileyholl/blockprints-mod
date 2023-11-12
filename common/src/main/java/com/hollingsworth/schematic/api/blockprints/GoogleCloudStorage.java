package com.hollingsworth.schematic.api.blockprints;

import com.hollingsworth.schematic.Constants;
import com.hollingsworth.schematic.api.SceneExporter;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class GoogleCloudStorage {

    public static String getBucketUrl() {
        if (Constants.isDev) {
            return "https://storage.googleapis.com/blockprints-dev/";
        }
        return "https://storage.googleapis.com/blockprints-prod/";
    }

    //TODO: convert to api response
    public static boolean uploadFileToGCS(URL signedUrl, Path filePath, String contentType, int fileSize) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(signedUrl.toString()))
                .header("Content-Type", contentType)
                .header("x-goog-content-length-range", "0," + fileSize)
                .PUT(HttpRequest.BodyPublishers.ofFile(filePath))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            System.out.println(response.body());
        }
        return response.statusCode() == 200;
    }

    public static ApiResponse<byte[]> downloadImage(String gcsPath) {
        return download(gcsPath);
    }

    public static ApiResponse<Path> downloadSchematic(String gcsPath, String fileName) {
        Path path = download(gcsPath, SceneExporter.STRUCTURE_FOLDER, fileName);
        if (path == null) {
            return ApiResponse.unexpectedFailure();
        }
        return ApiResponse.success(path);
    }

    public static ApiResponse<byte[]> download(String gcsPath) {
        var uri = URI.create(getBucketUrl() + gcsPath);
        try (InputStream in = uri.toURL().openStream()) {
            return ApiResponse.success(in.readAllBytes());
        } catch (IOException e) {
            e.printStackTrace();
            return ApiResponse.connectionError();
        }
    }

    public static Path download(String gcsPath, String folder, String fileName) {
        String sanitizedName = SceneExporter.sanitize(fileName);
        var uri = URI.create(getBucketUrl() + gcsPath);
        try (InputStream in = uri.toURL().openStream()) {

            var path = Paths.get(folder + "/" + sanitizedName + "/" + sanitizedName + ".nbt");
            Files.createDirectories(Paths.get(folder + "/" + sanitizedName));
            Files.copy(in, path, StandardCopyOption.REPLACE_EXISTING);
            return path;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
