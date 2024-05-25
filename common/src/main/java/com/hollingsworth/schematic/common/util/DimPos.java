package com.hollingsworth.schematic.common.util;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public record DimPos(ResourceKey<Level> levelKey, BlockPos pos) {
}
