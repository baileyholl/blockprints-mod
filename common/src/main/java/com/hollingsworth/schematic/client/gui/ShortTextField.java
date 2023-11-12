package com.hollingsworth.schematic.client.gui;

import com.hollingsworth.schematic.Constants;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ShortTextField extends NoShadowTextField {
    public ShortTextField(Font p_i232260_1_, int x, int y, Component p_i232260_6_) {
        super(p_i232260_1_, x, y, 90, 17, p_i232260_6_);
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        graphics.blit(new ResourceLocation(Constants.MOD_ID, "textures/gui/diologue_small_editable.png"), this.getX(), this.getY(), 0, 0, 95, 17, 95, 17);
        super.renderWidget(graphics, mouseX, mouseY, partialTicks);
    }
}
