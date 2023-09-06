package com.hollingsworth.schematic.export;

import org.jetbrains.annotations.Nullable;

public class WrappedScene {
    @Nullable
    private Scene scene;

    private final Viewport viewport = new Viewport();

    private SavedCameraSettings initialCameraSettings = new SavedCameraSettings();


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
