package com.hollingsworth.schematic.client;

import com.hollingsworth.nuggets.client.area_capture.AreaCaptureHandler;
import com.hollingsworth.nuggets.client.area_capture.RenderStructureHandler;
import com.hollingsworth.nuggets.client.rendering.StructureRenderer;
import com.hollingsworth.nuggets.common.util.WorldHelpers;
import com.hollingsworth.schematic.ClientConstants;
import com.hollingsworth.schematic.Constants;
import com.hollingsworth.schematic.client.gui.HomeScreen;
import com.hollingsworth.schematic.client.gui.UploadPreviewScreen;
import com.hollingsworth.schematic.client.renderer.BlockPrintsStructureData;
import com.hollingsworth.schematic.networking.PlaceSchematicPacket;
import com.hollingsworth.schematic.platform.Services;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ClientData {
    private static final String CATEGORY = "key.category." + Constants.MOD_ID + ".general";
    public static final KeyMapping OPEN_MENU = new KeyMapping("key." + Constants.MOD_ID + ".open_menu", GLFW.GLFW_KEY_GRAVE_ACCENT, CATEGORY);
    public static final KeyMapping TOOL_MENU = new KeyMapping("key." + Constants.MOD_ID + ".tool_menu", GLFW.GLFW_KEY_LEFT_ALT, CATEGORY);

    public static AreaCaptureHandler areaCaptureHandler = new AreaCaptureHandler(Constants.MOD_ID, TOOL_MENU,  (structureTemplate, areaCaptureHandler) -> {
        if(structureTemplate == null){
            return;
        }
        Minecraft.getInstance().setScreen(new UploadPreviewScreen(WorldHelpers.getStructure(Minecraft.getInstance().level, areaCaptureHandler.firstTarget, areaCaptureHandler.secondTarget), areaCaptureHandler.firstTarget, areaCaptureHandler.secondTarget));
    });

    public static List<BlockPrintsStructureData> structures = new ArrayList<>();

    public static RenderStructureHandler<BlockPrintsStructureData> renderStructureHandler = createRenderHandler();

    public static final KeyFunction[] KEY_FUNCTIONS = new KeyFunction[]{
            new KeyFunction(OPEN_MENU, ClientData::openMenu),
            new KeyFunction(TOOL_MENU, (keyEvent) -> {
                renderStructureHandler.toolKeyHit(keyEvent.isDown());
                areaCaptureHandler.toolKeyHit(keyEvent.isDown());
            })

    };

    private static RenderStructureHandler<BlockPrintsStructureData> createRenderHandler(){
        Consumer<RenderStructureHandler<BlockPrintsStructureData>> onPrint = null;
        if(Minecraft.getInstance().player != null && Minecraft.getInstance().player.isCreative() && ClientConstants.blockprintsServerside) {
            onPrint = (handler) -> {
                if(handler.placingData != null){
                    Services.PLATFORM.sendClientToServerPacket(new PlaceSchematicPacket(handler.placingData.structureTemplate, handler.placingData.structurePlaceSettings, handler.placingData.anchorPos.above(1)));
                }
            };

        }
        return new RenderStructureHandler<>(Constants.MOD_ID, TOOL_MENU, null, (handler) ->{
            // On confirmed placement, reset handler to dismiss it
            ClientData.renderStructureHandler = createRenderHandler();
        }, (handler) -> {
            // On Delete
            removeStructure(handler.placingData);
        }, onPrint);
    }

    public static void removeStructure(BlockPrintsStructureData data){
        if(data == null){
            return;
        }
        structures.remove(data);
        if(renderStructureHandler.placingData == data){
            renderStructureHandler.placingData = null;
        }
        data.close();
    }

    public static void onLogout(){
        if(!RenderSystem.isOnRenderThread()){
            RenderSystem.recordRenderCall(ClientData::onLogout);
            return;
        }
        for(BlockPrintsStructureData data : structures){
            data.close();
        }
        structures.clear();
        ClientConstants.blockprintsServerside = false;
        renderStructureHandler = createRenderHandler();
        areaCaptureHandler.cancelCapture();
    }

    public static void openMenu(KeyEvent event) {
        if(event.isDown()) {
            Minecraft.getInstance().setScreen(new HomeScreen());
        }
    }

    public static void startBoundaryCapture(){
        areaCaptureHandler.startCapture();
        removeStructure(renderStructureHandler.placingData);
        renderStructureHandler = createRenderHandler();
    }

    public static void startStructureRenderer(StructureTemplate structureTemplate, String name, String blockprintsId){
        areaCaptureHandler.cancelCapture();
        removeStructure(renderStructureHandler.placingData);
        renderStructureHandler = createRenderHandler();
        renderStructureHandler.placingData = new BlockPrintsStructureData(structureTemplate, name, blockprintsId);
        structures.add(renderStructureHandler.placingData);
    }

    public static void renderAfterSky(PoseStack poseStack, Matrix4f modelViewMatrix) {
        areaCaptureHandler.renderBoundary(poseStack, modelViewMatrix);
    }

    public static void renderAfterTransparentBlocks(PoseStack poseStack, Matrix4f projectionMatrix, Matrix4f modelViewMatrix){
        for(var data : structures){
            StructureRenderer.buildRender(data, poseStack, Minecraft.getInstance().player);
        }
        for(var data : structures){
            StructureRenderer.drawRender(data, poseStack, projectionMatrix, modelViewMatrix, Minecraft.getInstance().player);
        }
   }

   public static boolean mouseScrolled(double delta){
        return renderStructureHandler.mouseScrolled(delta) || areaCaptureHandler.mouseScrolled(delta);
   }


    public static void rightClickEvent() {
        areaCaptureHandler.rightClickEvent();
        if(renderStructureHandler.placingData != null) {
            renderStructureHandler.rightClickEvent();
        }
    }

    public static void renderGUIOverlayEvent(GuiGraphics graphics, Window window) {
        areaCaptureHandler.renderInstructions(graphics, window);
        renderStructureHandler.renderInstructions(graphics, window);
    }

    public static void tickEvent(){
        if(Minecraft.getInstance().player == null || Minecraft.getInstance().level == null){
            return;
        }
        renderStructureHandler.tick();
        areaCaptureHandler.tick();
    }

    public record KeyFunction(KeyMapping mapping, Consumer<KeyEvent> function){}
}
