package com.hollingsworth.schematic.api.blockprints.favorites;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class FavoritesResponse {
    public final List<Favorite> favorites;
    public final String lastId;
    public final int page;

    public FavoritesResponse(JsonObject jsonObject) {
        favorites = new ArrayList<>();
        this.lastId = jsonObject.get("lastId").getAsString();
        this.page = jsonObject.get("page").getAsInt();
        for (var favorite : jsonObject.get("favorites").getAsJsonArray()) {
            JsonObject object = favorite.getAsJsonObject();
            favorites.add(new Favorite(object.get("name").getAsString(),
                    object.get("id").getAsString(),
                    object.get("isFavorite").getAsBoolean(),
                    object.get("isBuild").getAsBoolean(),
                    object.get("isRecent").getAsBoolean()));
        }
    }

}
