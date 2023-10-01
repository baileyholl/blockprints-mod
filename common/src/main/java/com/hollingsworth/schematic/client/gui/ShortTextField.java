package com.hollingsworth.schematic.client.gui;

import com.hollingsworth.schematic.Constants;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class ShortTextField extends NoShadowTextField{
    public ShortTextField(Font p_i232260_1_, int x, int y, Component p_i232260_6_) {
        super(p_i232260_1_, x, y, 95, 15, p_i232260_6_);
    }

    public ShortTextField(Font p_i232259_1_, int x, int y, @Nullable EditBox box, Component component) {
        super(p_i232259_1_, x, y, 95, 15, box, component);
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        graphics.blit(new ResourceLocation(Constants.MOD_ID, "textures/gui/textbox_small.png"),this.getX(), this.getY(), 0, 0, this.getWidth(), this.getHeight(), this.getWidth(), this.getHeight());
        super.renderWidget(graphics, mouseX, mouseY, partialTicks);
    }
}
