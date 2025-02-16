package com.hollingsworth.schematic.client;

import com.hollingsworth.schematic.Constants;
import com.hollingsworth.schematic.client.gui.HomeScreen;
import com.hollingsworth.schematic.client.renderer.StructureRenderer;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;

public class ClientData {
    private static final String CATEGORY = "key.category." + Constants.MOD_ID + ".general";
    public static final KeyMapping OPEN_MENU = new KeyMapping("key." + Constants.MOD_ID + ".open_menu", GLFW.GLFW_KEY_GRAVE_ACCENT, CATEGORY);
    public static final KeyMapping CONFIRM = new KeyMapping("key." + Constants.MOD_ID + ".confirm_selection", GLFW.GLFW_KEY_ENTER, CATEGORY);
    public static final KeyMapping CANCEL = new KeyMapping("key." + Constants.MOD_ID + ".cancel_selection", GLFW.GLFW_KEY_BACKSPACE, CATEGORY);
    public static final KeyMapping ROTATE_LEFT = new KeyMapping("key." + Constants.MOD_ID + ".rotate_left", GLFW.GLFW_KEY_LEFT, CATEGORY);
    public static final KeyMapping ROTATE_RIGHT = new KeyMapping("key." + Constants.MOD_ID + ".rotate_right", GLFW.GLFW_KEY_RIGHT, CATEGORY);
    public static final KeyMapping TOOL_MENU = new KeyMapping("key." + Constants.MOD_ID + ".tool_menu", GLFW.GLFW_MOD_ALT, CATEGORY);

    public static final KeyFunction[] KEY_FUNCTIONS = new KeyFunction[]{
            new KeyFunction(OPEN_MENU, ClientData::openMenu),
            new KeyFunction(CONFIRM, ClientData::onConfirmHit),
            new KeyFunction(CANCEL, ClientData::onCancelHit),
            new KeyFunction(TOOL_MENU, RenderStructureHandler::toolKeyHit)

    };

    public static void openMenu(KeyEvent event) {
        if(event.isDown()) {
            Minecraft.getInstance().setScreen(new HomeScreen());
        }
    }

    public static void startBoundaryCapture(){
        AreaCaptureHandler.startCapture();
        RenderStructureHandler.cancelRender();
    }

    public static void startStructureRenderer(StructureTemplate structureTemplate, String name, String blockprintsId){
        RenderStructureHandler.startRender(structureTemplate, name, blockprintsId);
        AreaCaptureHandler.cancelCapture();
    }

    public static void onConfirmHit(KeyEvent event) {
        if(!event.isDown()){
            return;
        }
        AreaCaptureHandler.onConfirmHit();
    }

    public static void onCancelHit(KeyEvent event) {
        if(!event.isDown()){
            return;
        }
        AreaCaptureHandler.onCancelHit();
    }

    public static void renderAfterSky(PoseStack poseStack, Matrix4f modelViewMatrix) {
        AreaCaptureHandler.renderBoundary(poseStack, modelViewMatrix);
    }

    public static void renderAfterTransparentBlocks(PoseStack poseStack, Matrix4f projectionMatrix, Matrix4f modelViewMatrix){
        for(var data : StructureRenderer.structures){
            StructureRenderer.buildRender(data, poseStack, Minecraft.getInstance().player);
        }
        for(var data : StructureRenderer.structures){
            StructureRenderer.drawRender(data, poseStack, projectionMatrix, modelViewMatrix, Minecraft.getInstance().player);
        }
   }

   public static boolean mouseScrolled(double delta){
        return RenderStructureHandler.mouseScrolled(delta);
   }


    public static void rightClickEvent() {
        AreaCaptureHandler.positionClicked();
        RenderStructureHandler.positionClicked();
    }

    public static void renderGUIOverlayEvent(GuiGraphics graphics, Window window) {
        AreaCaptureHandler.renderBoundaryUI(graphics, window);
        RenderStructureHandler.renderInstructions(graphics, window);
    }

    public static void tickEvent(){
        RenderStructureHandler.tick();
    }

    public record KeyFunction(KeyMapping mapping, Consumer<KeyEvent> function){}
}
