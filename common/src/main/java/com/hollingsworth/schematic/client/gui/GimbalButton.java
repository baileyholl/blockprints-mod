package com.hollingsworth.schematic.client.gui;

import com.hollingsworth.schematic.Constants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class GimbalButton extends ANButton{
    String direction;

    public GimbalButton(int x, int y, String direction, OnPress onPress) {
        super(x, y, 5, 5, Component.empty(), onPress);
        this.direction = direction;
    }

    @Override
    public void render(GuiGraphics graphics, int parX, int parY, float partialTicks) {
        if(!this.visible){
            return;
        }
        graphics.blit(new ResourceLocation(Constants.MOD_ID, "textures/gui/gimbal/gimbal_" + direction + (isMouseOver(parX, parY) ? "_highlighted" : "_faded") + ".png"), x, y, 0, 0, width, height, width, height);
    }
}
