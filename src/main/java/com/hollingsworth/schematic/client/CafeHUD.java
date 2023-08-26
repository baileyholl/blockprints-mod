package com.hollingsworth.schematic.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.GameType;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class CafeHUD {
    public static final IGuiOverlay OVERLAY = CafeHUD::renderOverlay;

    public static int hoverTicks = 0;
    public static Object lastHovered = null;

    public static void renderOverlay(ForgeGui gui, PoseStack poseStack, float partialTicks, int width,
                                     int height) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui || mc.gameMode.getPlayerMode() == GameType.SPECTATOR || ClientInfo.cafeClientData == null || ClientInfo.ticksToShowHUD <= 0)
            return;
        mc.font.drawShadow(poseStack, "Score: " + ClientInfo.cafeClientData.score, 10, mc.getWindow().getGuiScaledHeight() - 45, 0xFFFFFF);
        mc.font.drawShadow(poseStack, "Num customers: " + ClientInfo.cafeClientData.customersRemaining, 10, mc.getWindow().getGuiScaledHeight() - 30, 0xFFFFFF);
        mc.font.drawShadow(poseStack, "State: " + ClientInfo.cafeClientData.smState, 10, mc.getWindow().getGuiScaledHeight() - 15, 0xFFFFFF);
    }
}
