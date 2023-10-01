package com.hollingsworth.schematic;

import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.client.renderer.MultiBufferSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Constants {
	public static final String MOD_ID = "blockprints";
	public static final String MOD_NAME = "Schematic Mod";
	public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);

	public static final int WHITE = 16777215;
	public static MultiBufferSource.BufferSource bufferSource = MultiBufferSource.immediate(new BufferBuilder(256));

}