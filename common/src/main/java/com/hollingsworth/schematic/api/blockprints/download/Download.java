package com.hollingsworth.schematic.api.blockprints.download;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hollingsworth.schematic.Constants;
import com.hollingsworth.schematic.api.SceneExporter;
import com.hollingsworth.schematic.api.blockprints.ApiResponse;
import com.hollingsworth.schematic.api.blockprints.GoogleCloudStorage;
import com.hollingsworth.schematic.api.blockprints.RequestUtil;
import net.minecraft.network.chat.Component;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;

public class Download {

    public static GetSchematicResponse getSchematic(String id) {
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
            return new GetSchematicResponse(responseObj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static GetDownloadResponse getSchematicDownloadUrl(String id) {
        HttpRequest request = RequestUtil.getBuilder()
                .uri(RequestUtil.getRoute("/api/v1/schematics/" + id + "/download"))
                .GET().build();
        try {
            var res = RequestUtil.CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            if (res.statusCode() != 200) {
                Constants.LOG.error(res.body());
                return null;
            }
            JsonObject responseObj = JsonParser.parseString(res.body()).getAsJsonObject();
            return new GetDownloadResponse(responseObj);
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

    public static ApiResponse<Path> downloadSchematic(String schematicId, String name) {
        var result = getSchematicDownloadUrl(schematicId);
        if (result == null) {
            return ApiResponse.error(Component.translatable("blockprints.download_not_found"));
        }
        var link = result.url;
        URI uri;
        try{
            uri = new URI(link);
        } catch (Exception e) {
            return ApiResponse.error(Component.translatable("blockprints.download_not_found"));
        }

        return ApiResponse.success(GoogleCloudStorage.downloadFromUrl(uri, SceneExporter.STRUCTURE_FOLDER, name + "_" + schematicId, ".nbt"));
    }
}
