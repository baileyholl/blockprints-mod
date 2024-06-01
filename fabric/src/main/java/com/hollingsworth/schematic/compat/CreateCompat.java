package com.hollingsworth.schematic.compat;

import com.simibubi.create.content.schematics.SchematicAndQuillItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class CreateCompat {
    public static void appendGlue(Level level, AABB aabb, CompoundTag tag){
        SchematicAndQuillItem.clampGlueBoxes(level, aabb, tag);
    }
}
