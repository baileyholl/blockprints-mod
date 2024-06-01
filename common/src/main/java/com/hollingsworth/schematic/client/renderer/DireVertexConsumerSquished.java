package com.hollingsworth.schematic.client.renderer;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import org.joml.Vector4f;

public class DireVertexConsumerSquished extends VertexConsumerWrapper {
    private final float minX, minY, minZ, maxX, maxY, maxZ;
    private final Matrix4f matrix4f;
    float minU = 0;
    float maxU = 1;
    float minV = 0;
    float maxV = 1;
    public boolean adjustUV = false;
    public boolean bottomUp = false;
    Direction direction = null;
    TextureAtlasSprite sprite;
    private float red = -1;
    private float green = -1;
    private float blue = -1;

    public DireVertexConsumerSquished(VertexConsumer parent, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, Matrix4f matrix4f) {
        super(parent);
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
        this.matrix4f = matrix4f;
    }

    public DireVertexConsumerSquished(VertexConsumer parent, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, Matrix4f matrix4f, float red, float green, float blue) {
        super(parent);
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
        this.matrix4f = matrix4f;
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    @Override
    public VertexConsumer color(int r, int g, int b, int a) {
        if (red == -1)
            parent.color(r, g, b, a);
        else {
            int rCol = (int) Mth.lerp(red, 0, r);
            int gCol = (int) Mth.lerp(green, 0, g);
            int bCol = (int) Mth.lerp(blue, 0, b);
            parent.color(rCol, gCol, bCol, a);
        }
        return this;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public void setSprite(TextureAtlasSprite sprite) {
        this.sprite = sprite;
        minU = sprite.getU0();
        maxU = sprite.getU1();
        minV = sprite.getV0();
        maxV = sprite.getV1();
    }

    @Override
    public VertexConsumer vertex(double x, double y, double z) {
        Matrix4f inverseMatrix = new Matrix4f(matrix4f);
        inverseMatrix.invert();

        Vector4f originalVector = inverseMatrix.transform(new Vector4f((float) x, (float) y, (float) z, 1.0f));
        float adjustedX = originalVector.x * (maxX - minX) + minX;
        float adjustedY = originalVector.y * (maxY - minY) + minY;
        float adjustedZ = originalVector.z * (maxZ - minZ) + minZ;

        Vector4f vector4f = matrix4f.transform(new Vector4f(adjustedX, adjustedY, adjustedZ, 1.0F));
        parent.vertex(vector4f.x, vector4f.y, vector4f.z);
        return this;
    }

    @Override
    public VertexConsumer uv(float u, float v) {
        if (adjustUV) {
            //Growing up from ground!
            if (bottomUp) {
                if (direction != null && !direction.getAxis().equals(Direction.Axis.Y)) {
                    float uDistanceFromStart = u - minU;  // How far is u from its starting value?
                    float vDistanceFromStart = v - minV;  // How far is v from its starting value?

                    float adjustedUDistance = uDistanceFromStart; // Adjust for the block width
                    float adjustedVDistance = vDistanceFromStart * (maxY);    // Adjust for the block height

                    float adjustedU = minU + adjustedUDistance;
                    float adjustedV = minV + adjustedVDistance;

                    parent.uv(adjustedU, adjustedV);
                } else {
                    parent.uv(u, v);
                }
            } else {
                //Building above!
                if (direction != null && !direction.getAxis().equals(Direction.Axis.Y)) {
                    float uDistanceToEnd = maxU - u;      // How far is u from its end value?
                    float vDistanceToEnd = maxV - v;      // How far is v from its end value?

                    float adjustedUDistance = uDistanceToEnd; // Adjust for the block height
                    float adjustedVDistance = vDistanceToEnd * (maxY); // Adjust for the block height

                    float adjustedU = maxU - adjustedUDistance; // Subtracting because we're adjusting from the end.
                    float adjustedV = maxV - adjustedVDistance; // Subtracting because we're adjusting from the end.
                    parent.uv(adjustedU, adjustedV);
                } else {
                    parent.uv(u, v);
                }
            }
        } else {
            parent.uv(u, v);
        }
        return this;
    }
}
