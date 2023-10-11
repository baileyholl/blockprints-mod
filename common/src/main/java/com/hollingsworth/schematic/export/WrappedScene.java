package com.hollingsworth.schematic.export;

import com.hollingsworth.schematic.export.level.FakeForwardingServerLevel;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.level.levelgen.SingleThreadedRandomSource;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.MemoryUtil;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.zip.GZIPInputStream;

public class WrappedScene {
    @Nullable
    public Scene scene;

    public void setScene(@Nullable Scene scene) {
        this.scene = scene;
        if (scene != null) {
            initialCameraSettings = scene.getCameraSettings().save();
        } else {
            initialCameraSettings = new SavedCameraSettings();
        }
    }

    public final Viewport viewport = new Viewport();

    private SavedCameraSettings initialCameraSettings = new SavedCameraSettings();

    public void placeStructure(Path filePath){
        try (DataInputStream stream = new DataInputStream(new BufferedInputStream(
                new GZIPInputStream(Files.newInputStream(filePath, StandardOpenOption.READ))))) {
            CompoundTag compoundTag = NbtIo.read(stream, new NbtAccounter(0x20000000L));
            var template = new StructureTemplate();
            var blocks = scene.getLevel().registryAccess().registryOrThrow(Registries.BLOCK).asLookup();
            template.load(blocks, compoundTag);
            placeStructure(template);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void placeStructure(StructureTemplate structureTemplate){
        var random = new SingleThreadedRandomSource(0L);
        var settings = new StructurePlaceSettings();
        settings.setIgnoreEntities(true); // Entities need a server level in structures
        structureTemplate.placeInWorld(new FakeForwardingServerLevel(scene.getLevel()), BlockPos.ZERO, BlockPos.ZERO, settings, random, 0);
    }

    public byte[] exportAsPng(float scale) {
        if (scene == null) {
            return null;
        }

        var prefSize = viewport.getPreferredSize();
        if (prefSize.width() <= 0 || prefSize.height() <= 0) {
            return null;
        }

        // We only scale the viewport, not scaling the view matrix means the scene will still fill it
        var width = (int) Math.max(1, prefSize.width() * scale);
        var height = (int) Math.max(1, prefSize.height() * scale);
        try (var osr = new OffScreenRenderer(width, height)) {
            return osr.captureAsPng(() -> {
                var renderer = GuidebookLevelRenderer.getInstance();
                scene.getCameraSettings().setViewportSize(prefSize);
                renderer.render(scene.getLevel(), scene.getCameraSettings());
            });
        }
    }

    public byte[] exportPreviewPng(){
        if (scene == null) {
            return null;
        }

        var prefSize = viewport.getPreferredSize();
        if (prefSize.width() <= 0 || prefSize.height() <= 0) {
            return null;
        }

        // Make a width and height where the max width is 400 and the max height is the same aspect ratio
        var maxWidth = 400;
        var originalWidth = prefSize.width();
        var originalHeight = prefSize.height();

        double scaleFactor = (double) maxWidth / originalWidth;
        int newHeight = (int) (originalHeight * scaleFactor);
        int newWidth = (int) (originalWidth * scaleFactor);
        try (var osr = new OffScreenRenderer(newWidth, newHeight)) {
            return osr.captureAsPng(() -> {
                var renderer = GuidebookLevelRenderer.getInstance();
                scene.getCameraSettings().setViewportSize(prefSize);
                renderer.render(scene.getLevel(), scene.getCameraSettings());
            });
        }
    }

    public void renderToCurrentTarget(LytSize size) {
        if (scene == null) {
            return;
        }

        var renderer = GuidebookLevelRenderer.getInstance();
        scene.getCameraSettings().setViewportSize(size);
        renderer.render(scene.getLevel(), scene.getCameraSettings());
    }

    public LytSize getPreferredSize() {
        if (scene == null) {
            return LytSize.empty();
        }

        return viewport.getPreferredSize();
    }

    public NativeImage asNativeImage(float scale){
        if(scene == null){
            return null;
        }
        var prefSize = viewport.getPreferredSize();
        if (prefSize.width() <= 0 || prefSize.height() <= 0) {
            return null;
        }

        // We only scale the viewport, not scaling the view matrix means the scene will still fill it
        var width = (int) Math.max(1, prefSize.width() * scale);
        var height = (int) Math.max(1, prefSize.height() * scale);
        byte[] bytes;
        try (var osr = new OffScreenRenderer(width, height)) {
            bytes = osr.captureAsPng(() -> {
                var renderer = GuidebookLevelRenderer.getInstance();
                scene.getCameraSettings().setViewportSize(prefSize);
                renderer.render(scene.getLevel(), scene.getCameraSettings());
            });
            ByteBuffer buffer = MemoryUtil.memAlloc(bytes.length);
            buffer.put(bytes);
            return NativeImage.read(buffer.flip());
        } catch (IOException e) {
            e.printStackTrace();
//            throw new RuntimeException(e);
        }
        return null;
    }

    class Viewport {

//
//        private boolean hideAnnotations;
//
//        // Indicates that hoveredAnnotation should be removed from the scene when it is no longer the hovered annotation
//        private boolean transientHoveredAnnotation;
//
//        // State for camera control
//        private int buttonDown;
//        private Vector2i pointDown;
//        private float initialRotY;
//        private float initialRotX;
//        private float initialTransX;
//        private float initialTransY;
//
//        protected LytRect computeLayout(LayoutContext context, int x, int y, int availableWidth) {
//            return bounds;
//        }
//
//
//        public void render(RenderContext context) {
//            if (background != null) {
//                context.fillRect(bounds, background);
//            }
//
//            if (scene == null) {
//                return;
//            }
//
//            var window = Minecraft.getInstance().getWindow();
//
//            // transform our document viewport into physical screen coordinates
//            var viewport = bounds.transform(context.poseStack().last().pose());
//            RenderSystem.viewport(
//                    (int) (viewport.x() * window.getGuiScale()),
//                    (int) (window.getHeight() - viewport.bottom() * window.getGuiScale()),
//                    (int) (viewport.width() * window.getGuiScale()),
//                    (int) (viewport.height() * window.getGuiScale()));
//
//            var renderer = GuidebookLevelRenderer.getInstance();
//
//            Collection<InWorldAnnotation> inWorldAnnotations;
//            if (hideAnnotations) {
//                // We still show transient annotations even if static annotations are hidden
//                if (transientHoveredAnnotation
//                        && hoveredAnnotation instanceof InWorldAnnotation hoveredInWorldAnnotation) {
//                    inWorldAnnotations = Collections.singletonList(hoveredInWorldAnnotation);
//                } else {
//                    inWorldAnnotations = Collections.emptyList();
//                }
//            } else {
//                inWorldAnnotations = scene.getInWorldAnnotations();
//            }
//            renderer.render(scene.getLevel(), scene.getCameraSettings(), inWorldAnnotations);
//
//            renderDebugCrosshairs();
//
//            RenderSystem.viewport(0, 0, window.getWidth(), window.getHeight());
//
//            context.pushScissor(bounds);
//
//            if (!hideAnnotations) {
//                renderOverlayAnnotations(scene, context);
//            }
//
//            context.popScissor();
//        }
//
//        /**
//         * Render one in 2D space at 0,0. And render one in 3D space at 0,0,0.
//         */
//        private void renderDebugCrosshairs() {
//
//            if (false) {
//                return;
//            }
//
//            RenderSystem.renderCrosshair(16);
//
//            RenderSystem.backupProjectionMatrix();
//            RenderSystem.setProjectionMatrix(scene.getCameraSettings().getProjectionMatrix(),
//                    VertexSorting.ORTHOGRAPHIC_Z);
//            var modelViewStack = RenderSystem.getModelViewStack();
//            modelViewStack.pushPose();
//            modelViewStack.setIdentity();
//            modelViewStack.mulPoseMatrix(scene.getCameraSettings().getViewMatrix());
//            RenderSystem.applyModelViewMatrix();
//
//            RenderSystem.renderCrosshair(2);
//            modelViewStack.popPose();
//            RenderSystem.applyModelViewMatrix();
//            RenderSystem.restoreProjectionMatrix();
//        }
//
//
//        public void setBounds(LytRect bounds) {
//            this.bounds = bounds;
//            if (scene != null) {
//                scene.getCameraSettings().setViewportSize(bounds.size());
//            }
//        }

        public LytSize getPreferredSize() {
            if (scene == null) {
                return LytSize.empty();
            }

            // Compute bounds using the *initial* camera settings
            var current = scene.getCameraSettings().save();
            scene.getCameraSettings().restore(initialCameraSettings);
            var screenBounds = scene.getScreenBounds();
            scene.getCameraSettings().restore(current);

            var width = (int) Math.ceil(Math.abs(screenBounds.z - screenBounds.x));
            var height = (int) Math.ceil(Math.abs(screenBounds.w - screenBounds.y));
            return new LytSize(width, height);
        }
    }
}
