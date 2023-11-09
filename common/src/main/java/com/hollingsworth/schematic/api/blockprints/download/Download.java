package com.hollingsworth.schematic.api.blockprints.download;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hollingsworth.schematic.Constants;
import com.hollingsworth.schematic.api.blockprints.ApiResponse;
import com.hollingsworth.schematic.api.blockprints.GoogleCloudStorage;
import com.hollingsworth.schematic.api.blockprints.RequestUtil;
import com.hollingsworth.schematic.client.ClientData;
import net.minecraft.network.chat.Component;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

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
            return new ApiResponse<PreviewDownloadResult>(Component.translatable("blockprints.download_not_found"));
        }
        var downloaded = GoogleCloudStorage.downloadImage(result.previewImage, result.structureName);
        if (downloaded == null) {
            return new ApiResponse<PreviewDownloadResult>(Component.translatable("blockprints.download_not_found"));
        }
        return new ApiResponse<PreviewDownloadResult>(new PreviewDownloadResult(result, downloaded));
    }

    public static ApiResponse<Boolean> downloadSchematic(String link, String name) {
        ClientData.setStatus(Component.translatable("blockprints.downloading_schematic"));
        var downloaded = GoogleCloudStorage.downloadSchematic(link, name);
        if (downloaded == null) {
            return new ApiResponse<>(Component.translatable("blockprints.download_not_found"));
        }
        return new ApiResponse<Boolean>(true);
    }
}
