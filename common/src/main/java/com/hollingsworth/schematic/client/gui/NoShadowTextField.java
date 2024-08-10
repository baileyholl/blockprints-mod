package com.hollingsworth.schematic.client.gui;

import com.hollingsworth.schematic.Constants;
import com.hollingsworth.schematic.mixin.EditBoxAccessor;
import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import javax.annotation.Nullable;
import java.util.function.Function;

public class NoShadowTextField extends EditBox {

    public Function<String, Void> onClear;

    public NoShadowTextField(Font p_i232260_1_, int p_i232260_2_, int p_i232260_3_, int p_i232260_4_, int p_i232260_5_, Component p_i232260_6_) {
        super(p_i232260_1_, p_i232260_2_, p_i232260_3_, p_i232260_4_, p_i232260_5_, p_i232260_6_);
    }

    public NoShadowTextField(Font p_i232259_1_, int p_i232259_2_, int p_i232259_3_, int p_i232259_4_, int p_i232259_5_, @Nullable EditBox p_i232259_6_, Component p_i232259_7_) {
        super(p_i232259_1_, p_i232259_2_, p_i232259_3_, p_i232259_4_, p_i232259_5_, p_i232259_6_, p_i232259_7_);
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        if(!this.visible){
            return;
        }
        EditBoxAccessor accessor = (EditBoxAccessor) this;
        int color = Constants.WHITE;
        int i2 = accessor.isIsEditable() ? accessor.getTextColor() : accessor.getTextColorUneditable();
        int j = accessor.getCursorPos() - accessor.getDisplayPos();
        int k = accessor.getHighlightPos() - accessor.getDisplayPos();
        String s = accessor.getFont().plainSubstrByWidth(accessor.getValue().substring(accessor.getDisplayPos()), this.getInnerWidth());
        boolean flag = j >= 0 && j <= s.length();
        boolean flag1 = this.isFocused() && (Util.getMillis() - accessor.getFocusedTime()) / 300L % 2L == 0L && flag;
        int l = accessor.isBordered() ? this.x + 4 : this.x;
        int i1 = accessor.isBordered() ? this.y + (this.height - 8) / 2 : this.y;
        int j1 = l;
        if (k > s.length()) {
            k = s.length();
        }
        if (!s.isEmpty()) {
            String s1 = flag ? s.substring(0, j) : s;
            j1 = graphics.drawString(accessor.getFont(), accessor.getFormatter().apply(s1, accessor.getDisplayPos()),  l,  i1, color, false);

        }

        boolean flag2 = accessor.getCursorPos() < accessor.getValue().length() || accessor.getValue().length() >= 32;
        int k1 = j1;
        if (!flag) {
            k1 = j > 0 ? l + this.width : l;
        } else if (flag2) {
            k1 = j1 - 1;
            --j1;
        }

        if (!s.isEmpty() && flag && j < s.length()) {
            graphics.drawString(accessor.getFont(), accessor.getFormatter().apply(s.substring(j), accessor.getCursorPos()), j1, i1, i2);
        }

        if (!flag2 && accessor.getSuggestion() != null) {
            graphics.drawString(accessor.getFont(), accessor.getSuggestion(), k1 - 1, i1, color, false);
        }

        if (flag1) {
            if (flag2) {
                graphics.fill(k1, i1 - 1, k1 + 1, i1 + 1 + 9, color);
            } else {
                graphics.drawString(accessor.getFont(), "_", k1, i1, i2, false);
            }
        }

    }

    @Override
    public boolean mouseClicked(double clickedX, double clickedY, int mouseButton) { // 0 for primary, 1 for secondary
        if (!this.isVisible()) {
            return false;
        } else {
            EditBoxAccessor accessor = (EditBoxAccessor) this;
            boolean clickedThis = clickedX >= (double) this.x && clickedX < (double) (this.x + this.width) && clickedY >= (double) this.y && clickedY < (double) (this.y + this.height);
            if (accessor.isCanLoseFocus()) {
                this.setFocused(clickedThis);
            }

            if (this.isFocused() && clickedThis && mouseButton == 0) {
                int i = Mth.floor(clickedX) - this.x;
                if (accessor.isBordered()) {
                    i -= 4;
                }

                String s = accessor.getFont().plainSubstrByWidth(accessor.getValue().substring(accessor.getDisplayPos()), this.getInnerWidth());
                this.moveCursorTo(accessor.getFont().plainSubstrByWidth(s, i).length() + accessor.getDisplayPos(), true);
                return true;
            } else if (this.isFocused() && mouseButton == 1) {
                if (accessor.getValue().isEmpty())
                    return clickedThis;


                if (onClear != null)
                    onClear.apply("");
                setValue("");
                return clickedThis;
            } else {
                return false;
            }
        }
    }
}
