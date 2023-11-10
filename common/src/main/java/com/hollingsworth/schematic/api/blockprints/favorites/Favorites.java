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
            return new ApiResponse<>(new FavoritesResponse(responseObj));
        } catch (URISyntaxException | IOException | InterruptedException e) {
            e.printStackTrace();
            return ApiResponse.expectedFailure();
        }
    }

}
