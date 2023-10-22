package com.hollingsworth.schematic.api.blockprints;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Upload {

    public static UploadResponse postUpload(String name, String description){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", name);
        jsonObject.addProperty("description", description);
        HttpRequest request =  RequestUtil.getBuilder()
                .uri(RequestUtil.getRoute("/api/v1/upload"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonObject.toString())).build();
        try {
            var res = RequestUtil.CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            JsonObject responseObj = JsonParser.parseString(res.body()).getAsJsonObject();
            return new UploadResponse(responseObj);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static boolean postDoneUploading(String id) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", id);
        HttpRequest request = RequestUtil.getBuilder()
                .uri(RequestUtil.getRoute("/api/v1/upload/complete"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonObject.toString())).build();
        try {
            var res = RequestUtil.CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            return res.statusCode() == 200;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

        public static class UploadResponse {
        public final String id;
        public final String[] signedImages;
        public final String signedSchematic;
        public final String signedPreviewImage;
        public final int imageFileSize;
        public final int schematicFileSize;
        public UploadResponse(String id, String[] signedImages, String signedSchematic, String signedPreviewImage, int imageFileSize, int schematicFileSize){
            this.id = id;
            this.signedImages = signedImages;
            this.signedSchematic = signedSchematic;
            this.signedPreviewImage = signedPreviewImage;
            this.imageFileSize = imageFileSize;
            this.schematicFileSize = schematicFileSize;
        }

        public UploadResponse(JsonObject jsonObject){
            JsonArray jsonArray = jsonObject.get("signedImages").getAsJsonArray();
            String[] signedImages = new String[jsonArray.size()];
            for(int i = 0; i < jsonArray.size(); i++){
                signedImages[i] = jsonArray.get(i).getAsString();
            }
            this.signedImages = signedImages;
            this.signedSchematic = jsonObject.get("signedSchematic").getAsString();
            this.signedPreviewImage = jsonObject.get("signedPreviewImage").getAsString();
            this.imageFileSize = jsonObject.get("imageFileSize").getAsInt();
            this.schematicFileSize = jsonObject.get("schematicFileSize").getAsInt();
            this.id = jsonObject.get("id").getAsString();
        }
    }
}
