package com.hollingsworth.schematic.mixin;

import com.mojang.blaze3d.platform.NativeImage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(NativeImage.class)
public interface NativeImageAccessor {

    @Accessor
    int getWidth();

    @Accessor
    int getHeight();

    @Accessor
    long getSize();
}
