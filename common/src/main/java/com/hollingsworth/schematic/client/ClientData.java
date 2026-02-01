package com.hollingsworth.schematic.client;

import com.hollingsworth.nuggets.client.area_capture.AreaCaptureHandler;
import com.hollingsworth.nuggets.client.gui.GuiHelpers;
import com.hollingsworth.schematic.Constants;
import com.hollingsworth.schematic.client.gui.HomeScreen;
import com.hollingsworth.schematic.client.gui.UploadPreviewScreen;
import com.hollingsworth.schematic.client.renderer.StructureRenderer;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;

public class ClientData {
    private static final String CATEGORY = "key.category." + Constants.MOD_ID + ".general";
    public static final KeyMapping OPEN_MENU = new KeyMapping("key." + Constants.MOD_ID + ".open_menu", GLFW.GLFW_KEY_GRAVE_ACCENT, CATEGORY);
    public static final KeyMapping CONFIRM = new KeyMapping("key." + Constants.MOD_ID + ".confirm_selection", GLFW.GLFW_KEY_ENTER, CATEGORY);
    public static final KeyMapping CANCEL = new KeyMapping("key." + Constants.MOD_ID + ".cancel_selection", GLFW.GLFW_KEY_BACKSPACE, CATEGORY);
    public static final KeyMapping TOOL_MENU = new KeyMapping("key." + Constants.MOD_ID + ".tool_menu", GLFW.GLFW_KEY_LEFT_ALT, CATEGORY);
    public static AreaCaptureHandler areaCaptureHandler = new AreaCaptureHandler((graphics, window, areaCaptureHandler) ->{
        boolean showBoundary = areaCaptureHandler.showBoundary;
        if (!showBoundary || Minecraft.getInstance().options.hideGui)
            return;
        BlockPos firstTarget = areaCaptureHandler.firstTarget;
        BlockPos secondTarget = areaCaptureHandler.secondTarget;
        float screenY = window.getGuiScaledHeight() / 2f;
        float screenX = window.getGuiScaledWidth() / 2f;
        float instructionY = window.getGuiScaledHeight() - 42;
        graphics.pose().pushPose();
        graphics.pose().translate(screenX, instructionY, 0);
        if (firstTarget != null && secondTarget != null) {
            GuiHelpers.drawCenteredOutlinedText(Minecraft.getInstance().font, graphics, Component.translatable(Constants.MOD_ID + ".expand_box" ), 0, -16);
            GuiHelpers.drawCenteredOutlinedText(Minecraft.getInstance().font, graphics, Component.translatable(Constants.MOD_ID + ".confirm_selection", CONFIRM.getTranslatedKeyMessage()).getVisualOrderText(), 0, 0);
        } else {
            String compKey = firstTarget == null ? "select_first" : "select_second";
            GuiHelpers.drawCenteredOutlinedText(Minecraft.getInstance().font, graphics, Component.translatable(Constants.MOD_ID + "." + compKey).getVisualOrderText(), 0, 0);
        }
        graphics.pose().popPose();
        graphics.pose().pushPose();
        graphics.pose().translate(screenX,  instructionY+ 10, 0);
        GuiHelpers.drawCenteredOutlinedText(Minecraft.getInstance().font, graphics, Component.translatable(Constants.MOD_ID + ".cancel_selection", CANCEL.getTranslatedKeyMessage()).getVisualOrderText(), 0, 0);
        graphics.pose().popPose();
    }, (structureTemplate, areaCaptureHandler) -> {
        Minecraft.getInstance().setScreen(new UploadPreviewScreen(structureTemplate, areaCaptureHandler.firstTarget, areaCaptureHandler.secondTarget));
    });

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
        areaCaptureHandler.startCapture();
        RenderStructureHandler.cancelRender();
    }

    public static void startStructureRenderer(StructureTemplate structureTemplate, String name, String blockprintsId){
        RenderStructureHandler.startRender(structureTemplate, name, blockprintsId);
        areaCaptureHandler.cancelCapture();
    }

    public static void onConfirmHit(KeyEvent event) {
        if(!event.isDown()){
            return;
        }
        areaCaptureHandler.onConfirmHit();
    }

    public static void onCancelHit(KeyEvent event) {
        if(!event.isDown()){
            return;
        }
        areaCaptureHandler.onCancelHit();
    }

    public static void renderAfterSky(PoseStack poseStack, Matrix4f modelViewMatrix) {
        areaCaptureHandler.renderBoundary(poseStack, modelViewMatrix);
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
        return RenderStructureHandler.mouseScrolled(delta) || areaCaptureHandler.mouseScrolled(delta);
   }


    public static void rightClickEvent() {
        areaCaptureHandler.positionClicked();
        RenderStructureHandler.positionClicked();
    }

    public static void renderGUIOverlayEvent(GuiGraphics graphics, Window window) {
        areaCaptureHandler.renderBoundaryUI(graphics, window);
        RenderStructureHandler.renderInstructions(graphics, window);
    }

    public static void tickEvent(){
        if(Minecraft.getInstance().player == null || Minecraft.getInstance().level == null){
            return;
        }
        RenderStructureHandler.tick();
        areaCaptureHandler.tick();
    }

    public record KeyFunction(KeyMapping mapping, Consumer<KeyEvent> function){}
}
