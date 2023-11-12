package com.hollingsworth.schematic.mixin;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexSorting;
import it.unimi.dsi.fastutil.ints.IntConsumer;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BufferBuilder.class)
public interface BufferBuilderAccessor {
    @Accessor
    Vector3f[] getSortingPoints();


    @Accessor
    VertexSorting getSorting();

    @Invoker
    IntConsumer callIntConsumer(int $$0, VertexFormat.IndexType $$1);

    @Accessor
    int getNextElementByte();

    @Accessor
    VertexFormat.Mode getMode();
}
