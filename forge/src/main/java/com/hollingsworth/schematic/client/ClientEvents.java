package com.hollingsworth.schematic.client;

import com.hollingsworth.schematic.Constants;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = Constants.MOD_ID)
@OnlyIn(Dist.CLIENT)
public class ClientEvents {

    private static final Minecraft MINECRAFT = Minecraft.getInstance();

    @SubscribeEvent
    public static void keyEvent(final InputEvent.Key event) {
        if (Minecraft.getInstance().player == null || InputConstants.PRESS != event.getAction() || MINECRAFT.screen != null )
            return;
        if(event.getKey() == ClientData.OPEN_MENU.getKey().getValue())
            ClientData.openMenu();
        if(event.getKey() == ClientData.CONFIRM.getKey().getValue()){
            ClientData.onConfirmHit();
        }
        if(event.getKey() == ClientData.CANCEL.getKey().getValue()){
            ClientData.onCancelHit();
        }
    }

    @SubscribeEvent
    public static void keyEvent(final PlayerInteractEvent.RightClickBlock event) {
        ClientData.positionClicked(event.getPos());
    }

    @SubscribeEvent
    public static void renderLast(final RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_SKY) {
            return;
        }
        ClientData.renderBoundary(event.getPoseStack());
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void afterRenderOverlayLayer(RenderGuiOverlayEvent.Post event) {
        if (event.getOverlay() != VanillaGuiOverlay.CROSSHAIR.type())
            return;
      ClientData.renderBoundaryUI(event.getGuiGraphics(), event.getWindow());
    }
}
