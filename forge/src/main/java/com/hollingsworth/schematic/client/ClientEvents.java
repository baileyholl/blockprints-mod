package com.hollingsworth.schematic.client;

import ca.weblite.objc.Client;
import com.hollingsworth.schematic.Constants;
import com.hollingsworth.schematic.client.renderer.StatePos;
import com.hollingsworth.schematic.client.renderer.StructureRenderer;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;

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
    }

    @SubscribeEvent
    public static void rightClickEvent(final InputEvent.MouseButton.Pre event) {
        if (InputConstants.MOUSE_BUTTON_RIGHT != event.getButton() || MINECRAFT.screen != null || event.getAction() != InputConstants.RELEASE)
            return;
        ClientData.positionClicked();
//        if(Minecraft.getInstance().player != null) {
//            StructureRenderer.statePosCache = new ArrayList<>();
//            StructureRenderer.statePosCache.add(new StatePos(Blocks.DIRT.defaultBlockState(), new BlockPos(0,0,0)));
//        }
    }

    @SubscribeEvent
    public static void renderLast(final RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            StructureRenderer.buildRender(event.getPoseStack(), MINECRAFT.player);
            StructureRenderer.drawRender(ClientData.anchorRenderPos, event.getPoseStack(), event.getProjectionMatrix(), MINECRAFT.player);
            return;
        }
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
