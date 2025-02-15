package com.hollingsworth.schematic.client.gui;

import com.hollingsworth.schematic.ClientConstants;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.DyeColor;

import java.util.Iterator;
import java.util.Objects;

public class GuiUtils {

    public static boolean isMouseInRelativeRange(int mouseX, int mouseY, AbstractWidget widget) {
        return isMouseInRelativeRange(mouseX, mouseY, widget.getX(), widget.getY(), widget.getWidth(), widget.getHeight());
    }

    public static boolean isMouseInRelativeRange(int mouseX, int mouseY, int x, int y, int w, int h) {
        return mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h;
    }

    //  https://github.com/Team-Resourceful/ResourcefulBees/blob/be1aa52925adfb42bf0fe90feeac011f7fc0d0db/common/src/main/java/com/teamresourceful/resourcefulbees/client/util/TextUtils.java#L43
    public static void drawCenteredStringNoShadow(Font font, GuiGraphics graphics, Component component, int x, int y, int color) {
        graphics.drawString(font, component.getString(), x - halfWidthOfText(font, component.getVisualOrderText()), y, color, false);
    }


    public static void drawOutlinedText(Font font, GuiGraphics graphics, Component component, int x, int y) {
        drawOutlinedText(font, graphics, component.getVisualOrderText(), x, y);
    }

    public static void drawOutlinedText(Font font, GuiGraphics graphics, FormattedCharSequence component, int x, int y) {
        font.drawInBatch8xOutline(component, x, y, DyeColor.WHITE.getTextColor(), DyeColor.BLACK.getTextColor(), graphics.pose().last().pose(), ClientConstants.bufferSource, 15728880);
        ClientConstants.bufferSource.endBatch();
    }

    public static void drawCenteredOutlinedText(Font font, GuiGraphics graphics, FormattedCharSequence component, int x, int y) {
        drawOutlinedText(font, graphics, component, x - halfWidthOfText(font, component), y);
    }

    public static void drawCenteredOutlinedText(Font font, GuiGraphics graphics, Component component, int x, int y) {
        drawCenteredOutlinedText(font, graphics, component.getVisualOrderText(), x, y);
    }

    public static void drawOutlinedWordWrap(GuiGraphics graphics, Font font, Component component, int x, int y, int wrapLength) {
        for (Iterator var7 = font.split(component, wrapLength).iterator(); var7.hasNext(); y += 9) {
            FormattedCharSequence $$6 = (FormattedCharSequence) var7.next();
            drawOutlinedText(font, graphics, $$6, x, y);
            Objects.requireNonNull(font);
        }

    }

    public static int halfWidthOfText(Font font, FormattedCharSequence component) {
        return font.width(component) / 2;
    }
}
