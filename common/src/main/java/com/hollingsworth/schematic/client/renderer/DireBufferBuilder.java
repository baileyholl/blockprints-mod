package com.hollingsworth.schematic.client.renderer;


import com.hollingsworth.schematic.mixin.BufferBuilderAccessor;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexFormat;
import org.apache.commons.lang3.ArrayUtils;

import java.util.function.IntConsumer;

public class DireBufferBuilder extends BufferBuilder {
    //This class exists because sorting in vanilla minecraft is the opposite of how we want to do it
    //So override the sort method (Which needs lots of ATs) and add a reversal line
    public DireBufferBuilder(int pCapacity) {
        super(pCapacity);
    }

    @Override
    public void putSortedQuadIndices(VertexFormat.IndexType pIndexType) {
        BufferBuilderAccessor bufferBuilderAccessor = (BufferBuilderAccessor) this;
        if (bufferBuilderAccessor.getSortingPoints() != null && bufferBuilderAccessor.getSorting() != null) {
            int[] aint = bufferBuilderAccessor.getSorting().sort(bufferBuilderAccessor.getSortingPoints());
            IntConsumer intconsumer = bufferBuilderAccessor.callIntConsumer(bufferBuilderAccessor.getNextElementByte(), pIndexType);
            // Reverse the order of the sorted indices. The whole reason this class exists is this one line!
            ArrayUtils.reverse(aint);
            var mode = bufferBuilderAccessor.getMode();
            var primitiveStride = mode.primitiveStride;
            for (int i : aint) {
                intconsumer.accept(i * primitiveStride + 0);
                intconsumer.accept(i * primitiveStride + 1);
                intconsumer.accept(i * primitiveStride + 2);
                intconsumer.accept(i * primitiveStride + 2);
                intconsumer.accept(i * primitiveStride + 3);
                intconsumer.accept(i * primitiveStride + 0);
            }

        } else {
            throw new IllegalStateException("Sorting state uninitialized");
        }
    }
}