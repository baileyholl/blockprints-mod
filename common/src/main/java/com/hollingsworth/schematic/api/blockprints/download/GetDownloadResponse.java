package com.hollingsworth.schematic.api.blockprints.download;

import com.google.gson.JsonObject;

public class GetDownloadResponse {
    public String url;

    public GetDownloadResponse(JsonObject jsonObject) {
        this.url = jsonObject.get("url").getAsString();
    }
}
