package com.hollingsworth.schematic.common.util;

import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public class DataSerializers {
    public static final EntityDataSerializer<Vec3> VEC3 = EntityDataSerializer.simple((buffer, vec) -> {
        buffer.writeDouble(vec.x);
        buffer.writeDouble(vec.y);
        buffer.writeDouble(vec.z);
    }, buffer -> new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble()));

    public static final EntityDataSerializer<ResourceLocation> RESOURCE_LOCATION = EntityDataSerializer.simple((buffer, vec) -> {
        buffer.writeUtf(vec.toString());
    }, buffer -> new ResourceLocation(buffer.readUtf()));

    public static void register(){
        EntityDataSerializers.registerSerializer(VEC3);
        EntityDataSerializers.registerSerializer(RESOURCE_LOCATION);
    }
}
