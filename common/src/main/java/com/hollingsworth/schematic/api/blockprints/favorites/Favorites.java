package com.hollingsworth.schematic.api.blockprints.favorites;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hollingsworth.schematic.api.blockprints.ApiResponse;
import com.hollingsworth.schematic.api.blockprints.RequestUtil;
import org.apache.http.client.utils.URIBuilder;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Favorites {
    public static ApiResponse<FavoritesResponse> getFavorites() {
        return getFavorites(true, true, true);
    }

    public static ApiResponse<FavoritesResponse> getFavorites(boolean getFavorites, boolean getBuilds, boolean getRecents) {

        try {
            URIBuilder uriBuilder = new URIBuilder(RequestUtil.getDomain() + "/api/v1/schematics/favorites");
            uriBuilder.setParameter("favorites", String.valueOf(getFavorites))
                    .setParameter("builds", String.valueOf(getBuilds))
                    .setParameter("recent", String.valueOf(getRecents));
            HttpRequest request = RequestUtil.getBuilder()
                    .uri(uriBuilder.build())
                    .GET().build();
            var res = RequestUtil.CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            var code = res.statusCode();
            if (!RequestUtil.responseSuccessful(code)) {
                return ApiResponse.parseServerError(res);
            }
            JsonObject responseObj = JsonParser.parseString(res.body()).getAsJsonObject();
            return ApiResponse.success(new FavoritesResponse(responseObj));
        } catch (URISyntaxException | IOException | InterruptedException e) {
            e.printStackTrace();
            return ApiResponse.connectionError();
        }
    }

    public static ApiResponse<Boolean> addFavorite(String id) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", id);
        HttpRequest request = RequestUtil.getBuilder()
                .uri(RequestUtil.getRoute("/api/v1/schematics/favorites"))
                .PUT(HttpRequest.BodyPublishers.ofString(jsonObject.toString())).build();
        try {
            var res = RequestUtil.CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            if (!RequestUtil.responseSuccessful(res.statusCode())) {
                return ApiResponse.parseServerError(res);
            }
            return ApiResponse.success();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return ApiResponse.connectionError();
        }
    }


    public static ApiResponse<Boolean> removeFavorite(String id) {
        HttpRequest request = RequestUtil.getBuilder(false)
                .uri(RequestUtil.getRoute("/api/v1/schematics/favorites/" + id))
                .DELETE().build();
        try {
            var res = RequestUtil.CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            if (!RequestUtil.responseSuccessful(res.statusCode())) {
                return ApiResponse.parseServerError(res);
            }
            return ApiResponse.success();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return ApiResponse.connectionError();
        }
    }

}
