package com.hollingsworth.schematic.client;

import com.hollingsworth.schematic.Constants;
import com.hollingsworth.schematic.api.blockprints.BlockprintsApi;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = Constants.MOD_ID)
@OnlyIn(Dist.CLIENT)
public class ClientEvents {

    private static final Minecraft MINECRAFT = Minecraft.getInstance();

    @SubscribeEvent
    public static void keyEvent(final InputEvent.Key event) {
        if (Minecraft.getInstance().player == null || InputConstants.PRESS != event.getAction() || MINECRAFT.screen != null)
            return;
        if (event.getKey() == ClientData.OPEN_MENU.getKey().getValue())
            ClientData.openMenu();
        if (event.getKey() == ClientData.CONFIRM.getKey().getValue()) {
            ClientData.onConfirmHit();
        }
        if (event.getKey() == ClientData.CANCEL.getKey().getValue()) {
            ClientData.onCancelHit();
        }
        if(event.getKey() == ClientData.ROTATE_LEFT.getKey().getValue()){
            ClientData.onRotateHit(false);
        }
        if(event.getKey() == ClientData.ROTATE_RIGHT.getKey().getValue()){
            ClientData.onRotateHit(true);
        }
    }

    @SubscribeEvent
    public static void rightClickEvent(final InputEvent.MouseButton.Pre event) {
        if (InputConstants.MOUSE_BUTTON_RIGHT != event.getButton() || MINECRAFT.screen != null || event.getAction() != InputConstants.RELEASE)
            return;
        ClientData.rightClickEvent();
    }

    @SubscribeEvent
    public static void renderLast(final RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            ClientData.renderAfterTransparentBlocks(event.getPoseStack(), event.getProjectionMatrix());
            return;
        }
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_SKY) {
            return;
        }
        ClientData.renderAfterSky(event.getPoseStack());
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void afterRenderOverlayLayer(RenderGuiOverlayEvent.Post event) {
        if (event.getOverlay() != VanillaGuiOverlay.CROSSHAIR.type())
            return;
        ClientData.renderGUIOverlayEvent(event.getGuiGraphics(), event.getWindow());

    }

    @SubscribeEvent
    public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut logOut){
        BlockprintsApi.clear();
    }
}
