package com.hollingsworth.schematic.api.blockprints.favorites;

public record Favorite(String name, String id, boolean isFavorite, boolean isBuild, boolean isRecent) {
}
