package com.hollingsworth.schematic.api.blockprints;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Upload {

    public static UploadResponse postUpload(String name, String description){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", name);
        jsonObject.addProperty("description", description);
        HttpRequest request =  RequestUtil.getBuilder()
                .uri(RequestUtil.getRoute("/upload"))
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

    public static class UploadResponse {
        public final String id;
        public final String[] signedImages;
        public final String signedSchematic;

        public UploadResponse(String id, String[] signedImages, String signedSchematic){
            this.id = id;
            this.signedImages = signedImages;
            this.signedSchematic = signedSchematic;
        }

        public UploadResponse(JsonObject jsonObject){
            JsonArray jsonArray = jsonObject.get("signedImages").getAsJsonArray();
            String[] signedImages = new String[jsonArray.size()];
            for(int i = 0; i < jsonArray.size(); i++){
                signedImages[i] = jsonArray.get(i).getAsString();
            }
            this.signedImages = signedImages;
            this.signedSchematic = jsonObject.get("signedSchematic").getAsString();
            this.id = jsonObject.get("id").getAsString();
        }
    }
}
