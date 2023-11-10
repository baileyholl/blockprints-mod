package com.hollingsworth.schematic.api.blockprints.upload;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hollingsworth.schematic.api.blockprints.ApiResponse;
import com.hollingsworth.schematic.api.blockprints.RequestUtil;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Upload {

    public static ApiResponse<UploadResponse> postUpload(String name, String description) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", name);
        jsonObject.addProperty("description", description);
        HttpRequest request = RequestUtil.getBuilder()
                .uri(RequestUtil.getRoute("/api/v1/upload"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonObject.toString())).build();
        try {
            var res = RequestUtil.CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            if (!RequestUtil.responseSuccessful(res.statusCode())) {
                return ApiResponse.parseServerError(res);
            }
            JsonObject responseObj = JsonParser.parseString(res.body()).getAsJsonObject();
            return new ApiResponse<UploadResponse>(new UploadResponse(responseObj));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ApiResponse.expectedFailure();
    }

    public static ApiResponse<Boolean> postEdit(String id, String name, String description) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", name);
        jsonObject.addProperty("description", description);
        HttpRequest request = RequestUtil.getBuilder()
                .uri(RequestUtil.getRoute("/api/v1/upload/" + id))
                .PUT(HttpRequest.BodyPublishers.ofString(jsonObject.toString())).build();
        try {
            var res = RequestUtil.CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            if (!RequestUtil.responseSuccessful(res.statusCode())) {
                return ApiResponse.parseServerError(res);
            }
            return ApiResponse.success();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return ApiResponse.expectedFailure();
        }
    }


    public static ApiResponse<Boolean> postDoneUploading(String id) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", id);
        HttpRequest request = RequestUtil.getBuilder()
                .uri(RequestUtil.getRoute("/api/v1/upload/complete"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonObject.toString())).build();
        try {
            var res = RequestUtil.CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            if (!RequestUtil.responseSuccessful(res.statusCode())) {
                return ApiResponse.parseServerError(res);
            }
            return new ApiResponse<>(true);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return ApiResponse.expectedFailure();
    }

}
