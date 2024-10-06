package com.hollingsworth.schematic.api.blockprints.upload;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hollingsworth.schematic.api.blockprints.ApiResponse;
import com.hollingsworth.schematic.api.blockprints.BlockprintsApi;
import com.hollingsworth.schematic.api.blockprints.RequestUtil;

import javax.annotation.Nullable;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Upload {

    private final BlockprintsApi api;
    private final HttpClient CLIENT;

    public Upload(BlockprintsApi api) {
        this.api = api;
        this.CLIENT = this.api.CLIENT;
    }

    public ApiResponse<UploadResponse> postUpload(String name, String description, @Nullable String json, boolean makePublic) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", name);
        jsonObject.addProperty("description", description);
        jsonObject.addProperty("makePublic", makePublic);
        if(json != null) {
            jsonObject.addProperty("json", json);
        }
        HttpRequest request = api.getBuilder()
                .uri(RequestUtil.getRoute("/api/v1/upload"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonObject.toString())).build();
        try {
            var res = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            if (!RequestUtil.responseSuccessful(res.statusCode())) {
                return ApiResponse.parseServerError(res);
            }
            JsonObject responseObj = JsonParser.parseString(res.body()).getAsJsonObject();
            return ApiResponse.success(new UploadResponse(responseObj));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ApiResponse.connectionError();
    }

    public ApiResponse<Boolean> postEdit(String id, String name, String description, boolean makePublic) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", name);
        jsonObject.addProperty("description", description);
        jsonObject.addProperty("makePublic", makePublic);
        HttpRequest request = api.getBuilder()
                .uri(RequestUtil.getRoute("/api/v1/upload/" + id))
                .PUT(HttpRequest.BodyPublishers.ofString(jsonObject.toString())).build();
        try {
            var res = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            if (!RequestUtil.responseSuccessful(res.statusCode())) {
                return ApiResponse.parseServerError(res);
            }
            return ApiResponse.success();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return ApiResponse.connectionError();
        }
    }


    public ApiResponse<Boolean> postDoneUploading(String id) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", id);
        HttpRequest request = api.getBuilder()
                .uri(RequestUtil.getRoute("/api/v1/upload/complete"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonObject.toString())).build();
        try {
            var res = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            if (!RequestUtil.responseSuccessful(res.statusCode())) {
                return ApiResponse.parseServerError(res);
            }
            return ApiResponse.success();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return ApiResponse.connectionError();
    }

    public ApiResponse<Boolean> postDelete(String id){
        HttpRequest request = api.getBuilder(false)
                .uri(RequestUtil.getRoute("/api/v1/upload/" + id))
                .DELETE().build();
        try {
            var res = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            if (!RequestUtil.responseSuccessful(res.statusCode())) {
                return ApiResponse.parseServerError(res);
            }
            return ApiResponse.success();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return ApiResponse.connectionError();
    }

}
