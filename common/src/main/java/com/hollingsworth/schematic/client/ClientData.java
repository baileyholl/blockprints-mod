package com.hollingsworth.schematic.client;

import com.hollingsworth.schematic.Constants;
import com.hollingsworth.schematic.client.gui.HomeScreen;
import com.hollingsworth.schematic.client.renderer.StructureRenderer;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;

import java.util.concurrent.atomic.AtomicReference;

public class ClientData {
    public static final AtomicReference<String> uploadStatus = new AtomicReference<>();
    public static BlockPos anchorRenderPos;
    private static final String CATEGORY = "key.category." + Constants.MOD_ID + ".general";
    public static final KeyMapping OPEN_MENU = new KeyMapping("key." + Constants.MOD_ID + ".open_menu", GLFW.GLFW_KEY_GRAVE_ACCENT, CATEGORY);
    public static final KeyMapping CONFIRM = new KeyMapping("key." + Constants.MOD_ID + ".confirm_selection", GLFW.GLFW_KEY_ENTER, CATEGORY);
    public static final KeyMapping CANCEL = new KeyMapping("key." + Constants.MOD_ID + ".cancel_selection", GLFW.GLFW_KEY_BACKSPACE, CATEGORY);
    public static final KeyMapping[] KEYS = new KeyMapping[]{OPEN_MENU, CONFIRM, CANCEL};

    public static void openMenu() {
        Minecraft.getInstance().setScreen(new HomeScreen());
    }

    public static void startBoundaryCapture(){
        AreaCaptureHandler.startCapture();
        RenderStructureHandler.cancelRender();
    }

    public static void startStructureRenderer(StructureTemplate structureTemplate){
        RenderStructureHandler.startRender(structureTemplate);
        AreaCaptureHandler.cancelCapture();
    }

    public static void onConfirmHit() {
        AreaCaptureHandler.onConfirmHit();
        RenderStructureHandler.onConfirmHit();
    }

    public static void onCancelHit() {
        AreaCaptureHandler.onCancelHit();
        RenderStructureHandler.onCancelHit();
    }

    public static void renderAfterSky(PoseStack poseStack) {
        AreaCaptureHandler.renderBoundary(poseStack);
    }

    public static void renderAfterTransparentBlocks(PoseStack poseStack, Matrix4f projectionMatrix){
        StructureRenderer.buildRender(poseStack, Minecraft.getInstance().player);
        StructureRenderer.drawRender(RenderStructureHandler.anchorPos, poseStack, projectionMatrix, Minecraft.getInstance().player);
    }


    public static void rightClickEvent() {
        AreaCaptureHandler.positionClicked();
        RenderStructureHandler.positionClicked();
    }

    public static void renderGUIOverlayEvent(GuiGraphics graphics, Window window) {
        AreaCaptureHandler.renderBoundaryUI(graphics, window);
    }
}
