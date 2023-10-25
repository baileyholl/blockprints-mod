package com.hollingsworth.schematic.api.blockprints;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hollingsworth.schematic.Constants;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

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

    public static class DownloadResponse {
        public final String previewImage;
        public final String playerName;
        public final String structureName;
        public final String description;
        public final List<String> mods;
        public final String schematicLink;

        public DownloadResponse(JsonObject jsonObject) {
            mods = new ArrayList<>();
            this.previewImage = jsonObject.get("smallPreviewImage").getAsString();
            this.playerName = jsonObject.get("playerName").getAsString();
            this.structureName = jsonObject.get("name").getAsString();
            this.description = jsonObject.get("description").getAsString();
            this.schematicLink = jsonObject.get("schematic").getAsString();
            for (var mod : jsonObject.get("mods").getAsJsonArray()) {
                mods.add(mod.getAsString());
            }
        }
    }


}
