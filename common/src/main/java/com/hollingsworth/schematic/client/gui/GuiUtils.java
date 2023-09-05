package com.hollingsworth.schematic.client.gui;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;

public class GuiUtils {

    public static boolean isMouseInRelativeRange(int mouseX, int mouseY, AbstractWidget widget){
        return isMouseInRelativeRange(mouseX, mouseY, widget.x, widget.y, widget.getWidth(), widget.getHeight());
    }

    public static boolean isMouseInRelativeRange(int mouseX, int mouseY, int x, int y, int w, int h) {
        return mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h;
    }

    //  https://github.com/Team-Resourceful/ResourcefulBees/blob/be1aa52925adfb42bf0fe90feeac011f7fc0d0db/common/src/main/java/com/teamresourceful/resourcefulbees/client/util/TextUtils.java#L43
    public static void drawCenteredStringNoShadow(Font font, GuiGraphics graphics, Component component, int x, int y, int color) {
        graphics.drawString(font, component.getString(), x - halfWidthOfText(font, component), y, color, false);
    }

    public static int halfWidthOfText(Font font, Component component) {
        return font.width(component)/2;
    }
}
