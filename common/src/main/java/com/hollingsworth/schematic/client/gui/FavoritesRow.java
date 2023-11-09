package com.hollingsworth.schematic.client.gui;

import com.hollingsworth.schematic.Constants;
import com.hollingsworth.schematic.api.blockprints.favorites.Favorite;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class FavoritesRow extends NestedWidget {
    Favorite favorite;
    ViewBuildsScreen viewBuildsScreen;

    public FavoritesRow(int x, int y, Favorite favorite, ViewBuildsScreen screen) {
        super(x, y, 236, 12, Component.empty());
        this.favorite = favorite;
        this.viewBuildsScreen = screen;
        renderables.add(new GuiImageButton(x + 224, y + 1, 11, 11, new ResourceLocation(Constants.MOD_ID, "textures/gui/button_edit.png"), button -> {

        }).withTooltip(Component.translatable("blockprints.edit")));
        renderables.add(new GuiImageButton(x + 211, y + 1, 11, 11, new ResourceLocation(Constants.MOD_ID, "textures/gui/button_copy.png"), button -> {
            Minecraft.getInstance().keyboardHandler.setClipboard(favorite.id());
        }).withTooltip(Component.translatable("blockprints.copy")));
        renderables.add(new GuiImageButton(x + 198, y + 1, 11, 11, new ResourceLocation(Constants.MOD_ID, "textures/gui/button_view.png"), button -> {
        }).withTooltip(Component.translatable("blockprints.view")));
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int i, int i1, float v) {
        graphics.drawString(Minecraft.getInstance().font, favorite.name(), x + 16, y + 3, 0x000000, false);
        if (viewBuildsScreen.showBuilds && favorite.isBuild()) {
            graphics.blit(new ResourceLocation(Constants.MOD_ID, "textures/gui/icon_my_builds.png"), x + 4, y + 3, 0, 0, 5, 7, 5, 7);
        } else if (viewBuildsScreen.showFavorites && favorite.isFavorite()) {
            graphics.blit(new ResourceLocation(Constants.MOD_ID, "textures/gui/icon_favorite_builds.png"), x + 4, y + 3, 0, 0, 7, 7, 7, 7);
        } else if (viewBuildsScreen.showRecent && favorite.isRecent()) {
            graphics.blit(new ResourceLocation(Constants.MOD_ID, "textures/gui/icon_favorite_builds.png"), x + 4, y + 3, 0, 0, 7, 7, 7, 7);
        }
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

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }
}
