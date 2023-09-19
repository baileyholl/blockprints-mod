package com.hollingsworth.schematic.client.gui;

import com.hollingsworth.schematic.common.util.ITooltipProvider;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

import java.util.List;

public class ANButton extends Button implements ITooltipProvider {

	public ANButton(int x, int y, int w, int h, Component text, OnPress onPress) {
		super(x, y, w, h, text, onPress, Button.DEFAULT_NARRATION);
	}

//	public void setX(int i) {
//		x = i;
//	}
//
//	public void setY(int i) {
//		y = i;
//	}
//
//	public int getX() {
//		return x;
//	}
//
//	public int getY() {
//		return y;
//	}
//
//	public void setPosition(int x, int y) {
//		this.x = x;
//		this.y = y;
//	}

	@Override
	public void getTooltip(List<Component> tooltip) {}
}
