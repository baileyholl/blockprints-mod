package com.hollingsworth.schematic.client.renderer;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceMetadata;

public class UnitTextureAtlasSprite extends TextureAtlasSprite {
    public static final ResourceLocation LOCATION = ResourceLocation.fromNamespaceAndPath("neoforge", "unit");
    public static final UnitTextureAtlasSprite INSTANCE = new UnitTextureAtlasSprite();

    private UnitTextureAtlasSprite() {
        super(LOCATION, new SpriteContents(LOCATION, new FrameSize(1, 1), new NativeImage(1, 1, false), ResourceMetadata.EMPTY), 1, 1, 0, 0);
    }

    public float getU(float u) {
        return u;
    }

    public float getV(float v) {
        return v;
    }
}
