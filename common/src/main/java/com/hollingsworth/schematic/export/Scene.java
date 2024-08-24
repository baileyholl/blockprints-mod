package com.hollingsworth.schematic.export;

import com.hollingsworth.schematic.export.level.GuidebookLevel;
import org.jetbrains.annotations.NotNull;
import org.joml.*;

public class Scene {

    private final GuidebookLevel level;

    private final CameraSettings cameraSettings;


    private int width;
    private int height;

    public Scene(GuidebookLevel level, CameraSettings cameraSettings) {
        this.level = level;
        this.cameraSettings = cameraSettings;
    }

    public Vector4f getScreenBounds() {
        var offx = cameraSettings.getOffsetX();
        var offy = cameraSettings.getOffsetY();
        cameraSettings.setOffsetX(0);
        cameraSettings.setOffsetY(0);
        var viewMatrix = cameraSettings.getViewMatrix();
        cameraSettings.setOffsetX(offx);
        cameraSettings.setOffsetY(offy);

        var result = getBounds(viewMatrix);

        return new Vector4f(
                result.min().x,
                result.min().y,
                result.max().x,
                result.max().y);
    }

    public void centerScene() {
        var bounds = getScreenBounds();
        var w = -(bounds.z - bounds.x) / 2;
        var h = -(bounds.w - bounds.y) / 2;
        cameraSettings.setOffsetX(w - bounds.x);
        cameraSettings.setOffsetY(h - bounds.y);
    }

    Bounds cachedBounds = null;

    @NotNull
    private Bounds getBounds(Matrix4f viewMatrix) {
        if (!level.hasFilledBlocks()) {
            return new Bounds(new Vector3f(), new Vector3f());
        }
        if(cachedBounds != null){
            return cachedBounds;
        }

        // This is doing more work than needed since touching blocks create unneeded corners
        var tmpPos = new Vector3f();
        var min = new Vector3f(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);
        var max = new Vector3f(Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY);
        level.getFilledBlocks().forEach(pos -> {
            for (var xCorner = 0; xCorner <= 1; xCorner++) {
                for (var yCorner = 0; yCorner <= 1; yCorner++) {
                    for (var zCorner = 0; zCorner <= 1; zCorner++) {
                        viewMatrix.transformPosition(
                                pos.getX() + xCorner,
                                pos.getY() + yCorner,
                                pos.getZ() + zCorner,
                                tmpPos);
                        min.min(tmpPos);
                        max.max(tmpPos);
                    }
                }
            }
        });
        cachedBounds = new Bounds(min, max);
        return cachedBounds;
    }

    private record Bounds(Vector3f min, Vector3f max) {
    }
    Vector3f cachedCenter = null;
    public Vector3fc getWorldCenter() {
        if(cachedCenter != null){
            return cachedCenter;
        }
        // This is doing more work than needed since touching blocks create unneeded corners
        var tmpPos = new Vector3f();
        var min = new Vector3f(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);
        var max = new Vector3f(Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY);
        level.getFilledBlocks().forEach(pos -> {
            var tmp = new Vector3f();
            for (var xCorner = 0; xCorner <= 1; xCorner++) {
                for (var yCorner = 0; yCorner <= 1; yCorner++) {
                    for (var zCorner = 0; zCorner <= 1; zCorner++) {
                        tmp.set(pos.getX(), pos.getY(), pos.getZ());
                        min.min(tmp);
                        max.max(tmp);

                        tmp.add(1, 1, 1);
                        min.min(tmp);
                        max.max(tmp);
                    }
                }
            }
        });
        var avg = new Vector3f(min);
        avg.add(max);
        avg.div(2);
        cachedCenter = avg;
        return cachedCenter;
    }

    public GuidebookLevel getLevel() {
        return level;
    }

    /**
     * The camera settings affect layout so this should be called before layout is done (or relayout should be
     * triggered).
     */
    public CameraSettings getCameraSettings() {
        return cameraSettings;
    }

    /**
     * Transforms from document coordinates (layout coordinate system) to coordinates in the screen space used by the
     * scene.
     */
    public Vector2f documentToScreen(LytRect viewport, LytPoint documentPoint) {
        var localX = (documentPoint.x() - viewport.x()) / viewport.width() * 2 - 1;
        var localY = -((documentPoint.y() - viewport.y()) / viewport.height() * 2 - 1);
        return new Vector2f(localX, localY);
    }

    /**
     * Transforms from normalized device coordinates to document coordinates based on the given viewport in that
     * coordinate system.
     */
    public LytPoint screenToDocument(Vector2f screen, LytRect viewport) {
        var x = viewport.x() + (screen.x + 1) / 2f * viewport.width();
        var y = viewport.y() + (-screen.y + 1) / 2f * viewport.height();
        return new LytPoint(x, y);
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }


}
