package com.hollingsworth.schematic;


import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.client.renderer.MultiBufferSource;

public class ClientConstants {
    public static MultiBufferSource.BufferSource bufferSource = MultiBufferSource.immediate(new BufferBuilder(256));
}
