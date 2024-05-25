package com.hollingsworth.schematic.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class NestedWidget extends AbstractWidget implements NestedRenderables {
    public List<AbstractWidget> renderables = new ArrayList<>();

    public NestedWidget(int $$0, int $$1, int $$2, int $$3, Component $$4) {
        super($$0, $$1, $$2, $$3, $$4);
    }

    @Override
    public List<AbstractWidget> getExtras() {
        return renderables;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int i, int i1, float v) {

    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    @Override
    public void onClick(double $$0, double $$1) {
        super.onClick($$0, $$1);
    }

    @Override
    public boolean mouseClicked(double $$0, double $$1, int $$2) {
        return super.mouseClicked($$0, $$1, $$2);
    }

    @Override
    public void playDownSound(SoundManager $$0) {

    }
}
