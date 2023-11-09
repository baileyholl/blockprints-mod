package com.hollingsworth.schematic.api.blockprints.upload;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class UploadResponse {
    public final String id;
    public final String[] signedImages;
    public final String signedSchematic;
    public final String signedPreviewImage;
    public final int imageFileSize;
    public final int schematicFileSize;

    public UploadResponse(JsonObject jsonObject) {
        JsonArray jsonArray = jsonObject.get("signedImages").getAsJsonArray();
        String[] signedImages = new String[jsonArray.size()];
        for (int i = 0; i < jsonArray.size(); i++) {
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
