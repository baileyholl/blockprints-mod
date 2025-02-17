package com.hollingsworth.schematic.client.gui;

import com.hollingsworth.schematic.Constants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;

public class VerticalSlider extends BaseSlider {
    Consumer<Integer> onChange;

    public VerticalSlider(int x, int y, double maxValue, double stepSize, int precision, Consumer<Integer> onChange) {
        super(x, y, 15, 140, Component.empty(), Component.empty(), 0, maxValue, 0, stepSize, precision, false);
        this.onChange = onChange;
    }

    public VerticalSlider(int x, int y, int width, int height, Component prefix, Component suffix, double minValue, double maxValue, double currentValue, boolean drawString) {
        super(x, y, width, height, prefix, suffix, minValue, maxValue, currentValue, drawString);
    }

    @Override
    protected void applyValue() {
        super.applyValue();
        onChange.accept(this.getValueInt());
    }


    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.blit(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/container_scroll.png"), x, y - 21, 0, 0, 15, 159, 15, 159);
        guiGraphics.blit(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/scroll_bar_vertical.png"), x + 3, y + (int) (this.value * (double) (this.height - 20)), 0, 0, 9, 15, 9, 15);
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
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        boolean flag = keyCode == GLFW.GLFW_KEY_LEFT;
        if (flag || keyCode == GLFW.GLFW_KEY_RIGHT) {
            if (this.minValue > this.maxValue)
                flag = !flag;
            float f = flag ? -1F : 1F;
            if (stepSize <= 0D)
                this.setSliderValue(this.value + (f / (this.height - 8)));
            else
                this.setValue(this.getValue() + f * this.stepSize);
        }

        return false;
    }

    @Override
    public void setValueFromMouse(double mouseX, double mouseY) {
        this.setSliderValue((mouseY - (this.getY() + 4)) / (this.height - 8));
    }
}
