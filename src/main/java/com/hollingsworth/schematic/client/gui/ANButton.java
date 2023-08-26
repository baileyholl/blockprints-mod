package com.hollingsworth.schematic.client.gui;

import com.hollingsworth.schematic.common.util.ITooltipProvider;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

import java.util.List;

public class ANButton extends Button implements ITooltipProvider {

	public ANButton(int x, int y, int w, int h, Component text, OnPress onPress) {
		super(x, y, w, h, text, onPress);
	}

	public void setX(int i) {
		x = i;
	}

	public void setY(int i) {
		y = i;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
		super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
		render(new GuiGraphics(pPoseStack), pMouseX, pMouseY, pPartialTick);
	}

	public void render(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
//		super.render(graphics, pMouseX, pMouseY, pPartialTick);
	}

	@Override
	public void getTooltip(List<Component> tooltip) {}
}
