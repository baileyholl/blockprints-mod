package com.hollingsworth.schematic.client.gui;

import com.hollingsworth.schematic.Constants;
import com.hollingsworth.schematic.client.ForgeGraphics;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public class DecoratedSlider extends BaseSlider{
    public boolean isPitch;
    Consumer<Integer> onChange;

    public DecoratedSlider(int x, int y, int width, int height, Component prefix, Component suffix, double minValue, double maxValue, double currentValue, double stepSize, int precision, boolean isPitch, Consumer<Integer> onChange) {
        super(x, y, width, height, prefix, suffix, minValue, maxValue, currentValue, stepSize, precision, true);
        this.onChange = onChange;
        this.isPitch = isPitch;
    }

    public DecoratedSlider(int x, int y, int width, int height, Component prefix, Component suffix, double minValue, double maxValue, double currentValue, boolean drawString) {
        super(x, y, width, height, prefix, suffix, minValue, maxValue, currentValue, drawString);
    }

    @Override
    public void renderTexture(GuiGraphics $$0, ResourceLocation $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7, int $$8, int $$9, int $$10) {
        super.renderTexture($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7, $$8, $$9, $$10);
    }

    @Override
    protected void applyValue() {
        super.applyValue();
        onChange.accept(this.getValueInt());
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        final Minecraft mc = Minecraft.getInstance();
        ForgeGraphics forgeGraphics = new ForgeGraphics(guiGraphics);
//        forgeGraphics.blitWithBorder(SLIDER_LOCATION, this.getX(), this.getY(), 0, getTextureY(), this.width, this.height, 200, 20, 2, 3, 2, 2);

//        forgeGraphics.blitWithBorder(SLIDER_LOCATION, this.getX() + (int)(this.value * (double)(this.width - 8)), this.getY(), 0, getHandleTextureY(), 8, this.height, 200, 20 , 2, 3, 2, 2);
        String image = isPitch ? "textures/gui/slider_container_pitch.png" : "textures/gui/slider_container_yaw.png";
        guiGraphics.blit(new ResourceLocation(Constants.MOD_ID, image), x-16, y, 0, 0,  143, 15, 143, 15);
        guiGraphics.blit(new ResourceLocation(Constants.MOD_ID, "textures/gui/slider.png"), x + 3 + (int)(this.value * (double)(this.width - 13)), getY() + 3, 0, 0,  15, 9, 15, 9);
        GuiUtils.drawCenteredOutlinedText(mc.font, guiGraphics, Component.literal(String.valueOf(this.getValueInt())).getVisualOrderText(), x + 116, y + 4);
//        renderScrollingString(guiGraphics, mc.font, 2, this.active ? 16777215 : 10526880 | Mth.ceil(this.alpha * 255.0F) << 24);
    }
}
