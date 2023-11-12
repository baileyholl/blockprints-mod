package com.hollingsworth.schematic.client.gui;

import com.hollingsworth.schematic.Constants;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractScrollWidget;
import net.minecraft.client.gui.components.MultilineTextField;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Iterator;
import java.util.Objects;
import java.util.function.Consumer;

public class NoScrollMultiText extends AbstractScrollWidget {
    private static final int CURSOR_INSERT_WIDTH = 1;
    private static final int CURSOR_INSERT_COLOR = -3092272;
    private static final String CURSOR_APPEND_CHARACTER = "_";
    private static final int TEXT_COLOR = -2039584;
    private static final int PLACEHOLDER_TEXT_COLOR = -857677600;
    private final Font font;
    private final Component placeholder;
    private final MultilineTextField textField;
    private int frame;
    public boolean editable;

    public NoScrollMultiText(Font $$0, int $$1, int $$2, int $$3, int $$4, Component $$5, Component $$6) {
        super($$1, $$2, $$3, $$4, $$6);
        this.font = $$0;
        this.placeholder = $$5;
        this.textField = new MultilineTextField($$0, $$3 - this.totalInnerPadding());
        this.textField.setCursorListener(this::scrollToCursor);
        editable = true;
    }

    public void setCharacterLimit(int $$0) {
        this.textField.setCharacterLimit($$0);
    }

    public void setValueListener(Consumer<String> $$0) {
        this.textField.setValueListener($$0);
    }

    public void setValue(String $$0) {
        this.textField.setValue($$0);
    }

    public String getValue() {
        return this.textField.value();
    }

    public void tick() {
        ++this.frame;
    }

    public void updateWidgetNarration(NarrationElementOutput $$0) {
        $$0.add(NarratedElementType.TITLE, Component.translatable("gui.narrate.editBox", new Object[]{this.getMessage(), this.getValue()}));
    }

    public boolean mouseClicked(double $$0, double $$1, int $$2) {
        if (super.mouseClicked($$0, $$1, $$2)) {
            return true;
        } else if (this.withinContentAreaPoint($$0, $$1) && $$2 == 0) {
            this.textField.setSelecting(Screen.hasShiftDown());
            this.seekCursorScreen($$0, $$1);
            return true;
        } else {
            return false;
        }
    }

    public boolean mouseDragged(double $$0, double $$1, int $$2, double $$3, double $$4) {
        if (super.mouseDragged($$0, $$1, $$2, $$3, $$4)) {
            return true;
        } else if (this.withinContentAreaPoint($$0, $$1) && $$2 == 0) {
            this.textField.setSelecting(true);
            this.seekCursorScreen($$0, $$1);
            this.textField.setSelecting(Screen.hasShiftDown());
            return true;
        } else {
            return false;
        }
    }

    public boolean keyPressed(int $$0, int $$1, int $$2) {
        return this.textField.keyPressed($$0);
    }

    public boolean charTyped(char $$0, int $$1) {
        if (editable && this.visible && this.isFocused() && SharedConstants.isAllowedChatCharacter($$0)) {
            this.textField.insertText(Character.toString($$0));
            return true;
        } else {
            return false;
        }
    }

    protected void renderContents(GuiGraphics $$0, int $$1, int $$2, float $$3) {
        String $$4 = this.textField.value();
        int color = Constants.WHITE;
        if ($$4.isEmpty() && !this.isFocused()) {
            $$0.drawWordWrap(this.font, this.placeholder, this.getX() + this.innerPadding(), this.getY() + this.innerPadding(), this.width - this.totalInnerPadding(), color);
        } else {
            int $$5 = this.textField.cursor();
            boolean $$6 = this.isFocused() && this.frame / 6 % 2 == 0;
            boolean $$7 = $$5 < $$4.length();
            int $$8 = 0;
            int $$9 = 0;
            int $$10 = this.getY() + this.innerPadding();

            int var10002;
            int var10004;
            for (Iterator var12 = this.textField.iterateLines().iterator(); var12.hasNext(); $$10 += 9) {
                MultilineTextField.StringView $$11 = (MultilineTextField.StringView) var12.next();
                Objects.requireNonNull(this.font);
                boolean $$12 = this.withinContentAreaTopBottom($$10, $$10 + 9);
                if ($$6 && $$7 && $$5 >= $$11.beginIndex() && $$5 <= $$11.endIndex()) {
                    if ($$12) {
                        $$8 = $$0.drawString(this.font, $$4.substring($$11.beginIndex(), $$5), this.getX() + this.innerPadding(), $$10, color, false) - 1;
                        var10002 = $$10 - 1;
                        int var10003 = $$8 + 1;
                        var10004 = $$10 + 1;
                        Objects.requireNonNull(this.font);
                        $$0.fill($$8, var10002, var10003, var10004 + 9, -3092272);
                        $$0.drawString(this.font, $$4.substring($$5, $$11.endIndex()), $$8, $$10, color, false);
                    }
                } else {
                    if ($$12) {
                        $$8 = $$0.drawString(this.font, $$4.substring($$11.beginIndex(), $$11.endIndex()), this.getX() + this.innerPadding(), $$10, color, false) - 1;
                    }

                    $$9 = $$10;
                }

                Objects.requireNonNull(this.font);
            }

            if ($$6 && !$$7) {
                Objects.requireNonNull(this.font);
                if (this.withinContentAreaTopBottom($$9, $$9 + 9) && editable) {
                    $$0.drawString(this.font, "_", $$8, $$9, color, false);
                }
            }

            if (this.textField.hasSelection()) {
                MultilineTextField.StringView $$13 = this.textField.getSelected();
                int $$14 = this.getX() + this.innerPadding();
                $$10 = this.getY() + this.innerPadding();
                Iterator var20 = this.textField.iterateLines().iterator();

                while (var20.hasNext()) {
                    MultilineTextField.StringView $$15 = (MultilineTextField.StringView) var20.next();
                    if ($$13.beginIndex() > $$15.endIndex()) {
                        Objects.requireNonNull(this.font);
                        $$10 += 9;
                    } else {
                        if ($$15.beginIndex() > $$13.endIndex()) {
                            break;
                        }

                        Objects.requireNonNull(this.font);
                        if (this.withinContentAreaTopBottom($$10, $$10 + 9)) {
                            int $$16 = this.font.width($$4.substring($$15.beginIndex(), Math.max($$13.beginIndex(), $$15.beginIndex())));
                            int $$18;
                            if ($$13.endIndex() > $$15.endIndex()) {
                                $$18 = this.width - this.innerPadding();
                            } else {
                                $$18 = this.font.width($$4.substring($$15.beginIndex(), $$13.endIndex()));
                            }

                            var10002 = $$14 + $$16;
                            var10004 = $$14 + $$18;
                            Objects.requireNonNull(this.font);
                            this.renderHighlight($$0, var10002, $$10, var10004, $$10 + 9);
                        }

                        Objects.requireNonNull(this.font);
                        $$10 += 9;
                    }
                }
            }

        }
    }

    protected void renderDecorations(GuiGraphics $$0) {
        super.renderDecorations($$0);
        if (this.textField.hasCharacterLimit()) {
            int $$1 = this.textField.characterLimit();
            Component $$2 = Component.translatable("gui.multiLineEditBox.character_limit", new Object[]{this.textField.value().length(), $$1});
            $$0.drawString(this.font, $$2, this.getX() + this.width - this.font.width($$2), this.getY() + this.height + 4, 10526880, false);
        }

    }

    public int getInnerHeight() {
        Objects.requireNonNull(this.font);
        return 9 * this.textField.getLineCount();
    }

    protected boolean scrollbarVisible() {
        return false;
    }

    protected double scrollRate() {
        Objects.requireNonNull(this.font);
        return 9.0 / 2.0;
    }

    private void renderHighlight(GuiGraphics $$0, int $$1, int $$2, int $$3, int $$4) {
        $$0.fill(RenderType.guiTextHighlight(), $$1, $$2, $$3, $$4, -16776961);
    }

    private void scrollToCursor() {
        double $$0x = this.scrollAmount();
        MultilineTextField var10000 = this.textField;
        Objects.requireNonNull(this.font);
        MultilineTextField.StringView $$1x = var10000.getLineView((int) ($$0x / 9.0));
        int var5;
        if (this.textField.cursor() <= $$1x.beginIndex()) {
            var5 = this.textField.getLineAtCursor();
            Objects.requireNonNull(this.font);
            $$0x = (double) (var5 * 9);
        } else {
            var10000 = this.textField;
            double var10001 = $$0x + (double) this.height;
            Objects.requireNonNull(this.font);
            MultilineTextField.StringView $$2x = var10000.getLineView((int) (var10001 / 9.0) - 1);
            if (this.textField.cursor() > $$2x.endIndex()) {
                var5 = this.textField.getLineAtCursor();
                Objects.requireNonNull(this.font);
                var5 = var5 * 9 - this.height;
                Objects.requireNonNull(this.font);
                $$0x = (double) (var5 + 9 + this.totalInnerPadding());
            }
        }

        this.setScrollAmount($$0x);
    }

    private double getDisplayableLineCount() {
        double var10000 = (double) (this.height - this.totalInnerPadding());
        Objects.requireNonNull(this.font);
        return var10000 / 9.0;
    }

    private void seekCursorScreen(double $$0, double $$1) {
        double $$2 = $$0 - (double) this.getX() - (double) this.innerPadding();
        double $$3 = $$1 - (double) this.getY() - (double) this.innerPadding() + this.scrollAmount();
        this.textField.seekCursorToPoint($$2, $$3);
    }

    @Override
    protected void renderBackground(GuiGraphics graphics) {
        graphics.blit(new ResourceLocation(Constants.MOD_ID, "textures/gui/diologue_large_editable.png"), x, y, 0, 0, width, height, width, height);
    }
}
