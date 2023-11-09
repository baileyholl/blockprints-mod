package com.hollingsworth.schematic.api.blockprints.download;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hollingsworth.schematic.Constants;
import com.hollingsworth.schematic.api.blockprints.RequestUtil;

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


}
