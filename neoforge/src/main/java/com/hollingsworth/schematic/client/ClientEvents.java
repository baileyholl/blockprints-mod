package com.hollingsworth.schematic.client;

import com.hollingsworth.schematic.Constants;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

@EventBusSubscriber(value = Dist.CLIENT, modid = Constants.MOD_ID)
public class ClientEvents {

    private static final Minecraft MINECRAFT = Minecraft.getInstance();

    @SubscribeEvent
    public static void keyEvent(final InputEvent.Key event) {
        if (Minecraft.getInstance().player == null || MINECRAFT.screen != null)
            return;

        for(ClientData.KeyFunction keyMapping : ClientData.KEY_FUNCTIONS){
            if(!keyMapping.mapping().isUnbound() && keyMapping.mapping().getKey().getValue() == event.getKey()){
                keyMapping.function().accept(new KeyEvent(keyMapping.mapping(), event.getAction()));
            }
        }
    }

    @SubscribeEvent
    public static void rightClickEvent(final InputEvent.MouseButton.Pre event) {
        if (InputConstants.MOUSE_BUTTON_RIGHT != event.getButton() || MINECRAFT.screen != null || event.getAction() != InputConstants.RELEASE)
            return;
        ClientData.rightClickEvent();
    }

    @SubscribeEvent
    public static void scrollEvent(final InputEvent.MouseScrollingEvent event) {
        var cancel = ClientData.mouseScrolled(event.getScrollDeltaY());
        event.setCanceled(cancel);
    }

    @SubscribeEvent
    public static void renderLast(final RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            ClientData.renderAfterTransparentBlocks(event.getPoseStack(), event.getProjectionMatrix(), event.getModelViewMatrix());
            return;
        }
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_SKY) {
            return;
        }
        ClientData.renderAfterSky(event.getPoseStack(), event.getModelViewMatrix());
    }

    @SubscribeEvent
    public static void afterRenderOverlayLayer(RenderGuiLayerEvent.Post event) {
        if (event.getName().equals(VanillaGuiLayers.CROSSHAIR))
            return;
        ClientData.renderGUIOverlayEvent(event.getGuiGraphics(), Minecraft.getInstance().getWindow());
    }

    @SubscribeEvent
    public static void onTick(ClientTickEvent.Post event) {
        ClientData.tickEvent();
    }
}
