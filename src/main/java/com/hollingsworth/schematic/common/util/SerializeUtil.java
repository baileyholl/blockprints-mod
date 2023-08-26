package com.hollingsworth.schematic.common.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.AABB;

public class SerializeUtil {

    public static CompoundTag aabbToTag(AABB aabb){
        CompoundTag tag = new CompoundTag();
        tag.putDouble("minX", aabb.minX);
        tag.putDouble("minY", aabb.minY);
        tag.putDouble("minZ", aabb.minZ);
        tag.putDouble("maxX", aabb.maxX);
        tag.putDouble("maxY", aabb.maxY);
        tag.putDouble("maxZ", aabb.maxZ);
        return tag;
    }

    public static AABB aabbFromTag(CompoundTag tag){
        return new AABB(
                tag.getDouble("minX"),
                tag.getDouble("minY"),
                tag.getDouble("minZ"),
                tag.getDouble("maxX"),
                tag.getDouble("maxY"),
                tag.getDouble("maxZ")
        );
    }
}
