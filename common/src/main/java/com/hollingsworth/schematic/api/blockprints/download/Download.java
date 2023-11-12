package com.hollingsworth.schematic.api.blockprints.download;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hollingsworth.schematic.Constants;
import com.hollingsworth.schematic.api.blockprints.ApiResponse;
import com.hollingsworth.schematic.api.blockprints.GoogleCloudStorage;
import com.hollingsworth.schematic.api.blockprints.RequestUtil;
import net.minecraft.network.chat.Component;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;

public class Download {

    public static DownloadResponse getSchematic(String id) {
        HttpRequest request = RequestUtil.getBuilder()
                .uri(RequestUtil.getRoute("/api/v1/schematics/" + id))
                .GET().build();
        try {
            var res = RequestUtil.CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            if (res.statusCode() != 200) {
                Constants.LOG.error(res.body());
                return null;
            }
            JsonObject responseObj = JsonParser.parseString(res.body()).getAsJsonObject();
            return new DownloadResponse(responseObj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static ApiResponse<PreviewDownloadResult> downloadPreview(String id) {
        var result = getSchematic(id);
        if (result == null) {
            return ApiResponse.error(Component.translatable("blockprints.download_not_found"));
        }
        var downloaded = GoogleCloudStorage.downloadImage(result.previewImage);
        if (!downloaded.wasSuccessful()) {
            return ApiResponse.error(Component.translatable("blockprints.download_not_found"));
        }
        return ApiResponse.success(new PreviewDownloadResult(result, downloaded.response));
    }

    public static ApiResponse<Path> downloadSchematic(String link, String name) {
        return GoogleCloudStorage.downloadSchematic(link, name);
    }

    public static ApiResponse<Boolean> pushRecentDownload(String id){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", id);
        HttpRequest request = RequestUtil.getBuilder()
                .uri(RequestUtil.getRoute("/api/v1/schematics/recent"))
                .PUT(HttpRequest.BodyPublishers.ofString(jsonObject.toString())).build();
        try{
            var res = RequestUtil.CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            if (!RequestUtil.responseSuccessful(res.statusCode())) {
                return ApiResponse.parseServerError(res);
            }
            return ApiResponse.success();
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.connectionError();
        }
    }
}
