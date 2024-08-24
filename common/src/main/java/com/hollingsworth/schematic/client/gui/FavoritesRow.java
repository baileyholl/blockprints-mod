package com.hollingsworth.schematic.client.gui;

import com.hollingsworth.schematic.Constants;
import com.hollingsworth.schematic.api.blockprints.BlockprintsApi;
import com.hollingsworth.schematic.api.blockprints.favorites.Favorite;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class FavoritesRow extends NestedWidget {
    Favorite favorite;
    ViewFavoritesScreen viewBuildsScreen;

    public FavoritesRow(int x, int y, Favorite favorite, ViewFavoritesScreen screen) {
        super(x, y, 236, 12, Component.empty());
        this.favorite = favorite;
        this.viewBuildsScreen = screen;
        if(favorite.isBuild()) {
            renderables.add(new GuiImageButton(x + 224, y + 1, 11, 11, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/button_edit.png"), button -> {
                Minecraft.getInstance().setScreen(EditBuildScreen.getTransition(favorite.id(), viewBuildsScreen));
            }).withTooltip(Component.translatable("blockprints.edit")));
        }else if(favorite.isFavorite()){
            renderables.add(new GuiImageButton(x + 224, y + 1, 11, 11, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/button_remove_favorite.png"), button -> {
                Minecraft.getInstance().setScreen(new LoadingScreen<>(() -> BlockprintsApi.getInstance().favorites().removeFavorite(favorite.id()), (res) ->{
                    Minecraft.getInstance().setScreen(ViewFavoritesScreen.getTransition(viewBuildsScreen.showFavorites, viewBuildsScreen.showBuilds, viewBuildsScreen.showRecent));
                }));
            }).withTooltip(Component.translatable("blockprints.remove_favorite")));
        }else{
            renderables.add(new GuiImageButton(x + 224, y + 1, 11, 11, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/button_add_favorite.png"), button -> {
                Minecraft.getInstance().setScreen(new LoadingScreen<>(() ->  BlockprintsApi.getInstance().favorites().addFavorite(favorite.id()), (res) ->{
                    Minecraft.getInstance().setScreen(ViewFavoritesScreen.getTransition(viewBuildsScreen.showFavorites, viewBuildsScreen.showBuilds, viewBuildsScreen.showRecent));
                }));
            }).withTooltip(Component.translatable("blockprints.add_favorite")));
        }
        renderables.add(new GuiImageButton(x + 211, y + 1, 11, 11, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/button_copy.png"), button -> {
            Minecraft.getInstance().keyboardHandler.setClipboard(favorite.id());
        }).withTooltip(Component.translatable("blockprints.copy")));
        renderables.add(new GuiImageButton(x + 198, y + 1, 11, 11, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/button_view.png"), button -> {
            Minecraft.getInstance().setScreen(DownloadScreen.getTransition(favorite.id(), viewBuildsScreen));
        }).withTooltip(Component.translatable("blockprints.view")));
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int i, int i1, float v) {
        graphics.drawString(Minecraft.getInstance().font, favorite.name(), x + 16, y + 3, 0x000000, false);
        if (viewBuildsScreen.showBuilds && favorite.isBuild()) {
            graphics.blit(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/icon_my_builds.png"), x + 4, y + 3, 0, 0, 5, 7, 5, 7);
        } else if (viewBuildsScreen.showFavorites && favorite.isFavorite()) {
            graphics.blit(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/icon_favorite_builds.png"), x + 4, y + 3, 0, 0, 7, 7, 7, 7);
        } else if (viewBuildsScreen.showRecent && favorite.isRecent()) {
            graphics.blit(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/icon_recent_builds.png"), x + 4, y + 3, 0, 0, 7, 7, 7, 7);
        }
    }
}
