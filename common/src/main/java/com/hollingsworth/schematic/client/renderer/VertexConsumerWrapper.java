package com.hollingsworth.schematic.client.renderer;


import com.mojang.blaze3d.vertex.VertexConsumer;

/**
 * Wrapper for {@link VertexConsumer} which delegates all operations to its parent.
 * <p>
 * Useful for defining custom pipeline elements that only process certain data.
 */
public abstract class VertexConsumerWrapper implements VertexConsumer {
    protected final VertexConsumer parent;

    public VertexConsumerWrapper(VertexConsumer parent) {
        this.parent = parent;
    }

    public VertexConsumer addVertex(float x, float y, float z) {
        this.parent.addVertex(x, y, z);
        return this;
    }

    public VertexConsumer setColor(int r, int g, int b, int a) {
        this.parent.setColor(r, g, b, a);
        return this;
    }

    public VertexConsumer setUv(float u, float v) {
        this.parent.setUv(u, v);
        return this;
    }

    public VertexConsumer setUv1(int u, int v) {
        this.parent.setUv1(u, v);
        return this;
    }

    public VertexConsumer setUv2(int u, int v) {
        this.parent.setUv2(u, v);
        return this;
    }

    public VertexConsumer setNormal(float x, float y, float z) {
        this.parent.setNormal(x, y, z);
        return this;
    }
}