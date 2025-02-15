package com.hollingsworth.schematic.client;

import net.minecraft.resources.ResourceLocation;

public record BlitInfo(ResourceLocation location, int u, int v, int width, int height) {
    public BlitInfo(ResourceLocation location, int width, int height){
        this(location, 0, 0, width, height);
    }
}