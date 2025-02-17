package com.hollingsworth.schematic.client.gui;

import com.hollingsworth.schematic.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public class HorizontalSlider extends BaseSlider {
    public boolean isPitch;
    Consumer<Integer> onChange;

    public HorizontalSlider(int x, int y, Component prefix, Component suffix, double minValue, double maxValue, double currentValue, double stepSize, int precision, boolean isPitch, Consumer<Integer> onChange) {
        super(x, y, 95, 15, prefix, suffix, minValue, maxValue, currentValue, stepSize, precision, true);
        this.onChange = onChange;
        this.isPitch = isPitch;
    }

    @Override
    protected void applyValue() {
        super.applyValue();
        onChange.accept(this.getValueInt());
    }

    @Override
    public void setValueFromMouse(double mouseX, double mouseY) {
        this.setSliderValue((mouseX - (double)(this.getX() + 4)) / (double)(this.width - 8));
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pScrollX, double pScrollY) {
        if(pScrollY != 0){
            this.setValue(this.getValue() + (pScrollY > 0 ? -1 : 1) * stepSize);
            applyValue();
            return true;
        }
        return super.mouseScrolled(pMouseX, pMouseY, pScrollX, pScrollY);
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        final Minecraft mc = Minecraft.getInstance();
        String image = isPitch ? "textures/gui/container_scroll_pitch.png" : "textures/gui/container_scroll_yaw.png";
        guiGraphics.blit(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, image), x - 16, y, 0, 0, 143, 15, 143, 15);
        guiGraphics.blit(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/scroll_bar_horizontal.png"), x + 1 + (int) (this.value * (double) (this.width - 9)), getY() + 3, 0, 0, 15, 9, 15, 9);
        GuiUtils.drawCenteredOutlinedText(mc.font, guiGraphics, Component.literal(String.valueOf(this.getValueInt())).getVisualOrderText(), x + 116, y + 4);
    }
}
