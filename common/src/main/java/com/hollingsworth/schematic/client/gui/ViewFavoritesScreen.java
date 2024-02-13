package com.hollingsworth.schematic.client.gui;

import com.hollingsworth.schematic.Constants;
import com.hollingsworth.schematic.api.blockprints.favorites.Favorite;
import com.hollingsworth.schematic.api.blockprints.favorites.Favorites;
import com.hollingsworth.schematic.api.blockprints.favorites.FavoritesResponse;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class ViewFavoritesScreen extends BaseSchematicScreen {
    protected FavoritesResponse res = null;
    List<Favorite> favorites = new ArrayList<>();
    List<FavoritesRow> rows = new ArrayList<>();
    public boolean showFavorites = true;
    public boolean showBuilds = true;
    public boolean showRecent = true;
    int scroll = 0;
    public ViewFavoritesScreen(FavoritesResponse favoritesResponse) {
        this(favoritesResponse, true, true, true);
    }

    public static LoadingScreen<FavoritesResponse> getTransition() {
        return new LoadingScreen<>(Favorites::getFavorites, (favorites) -> {
            Minecraft.getInstance().setScreen(new ViewFavoritesScreen(favorites));
        });
    }

    public static LoadingScreen<FavoritesResponse> getTransition(ViewFavoritesScreen previousScreen) {
        return new LoadingScreen<>(Favorites::getFavorites, (favorites) -> {
            Minecraft.getInstance().setScreen(new ViewFavoritesScreen(favorites, previousScreen.showFavorites, previousScreen.showBuilds, previousScreen.showRecent));
        }, previousScreen);
    }

    public static LoadingScreen<FavoritesResponse> getTransition(boolean showFavorites, boolean showBuilds, boolean showRecent) {
        return new LoadingScreen<>(() -> Favorites.getFavorites(showFavorites, showBuilds, showRecent), (favorites) -> {
            Minecraft.getInstance().setScreen(new ViewFavoritesScreen(favorites, showFavorites, showBuilds, showRecent));
        }, null, 30);
    }

    public ViewFavoritesScreen(FavoritesResponse favoritesResponse, boolean showFavorites, boolean showBuilds, boolean showRecent) {
        super();
        this.favorites = favoritesResponse.favorites;
        this.res = favoritesResponse;
        this.showFavorites = showFavorites;
        this.showBuilds = showBuilds;
        this.showRecent = showRecent;
        // Sort by builds, then favorites, then recent
        favorites.sort((o1, o2) -> {
            if (o1.isBuild() && !o2.isBuild()) {
                return -1;
            } else if (!o1.isBuild() && o2.isBuild()) {
                return 1;
            } else if (o1.isFavorite() && !o2.isFavorite()) {
                return -1;
            } else if (!o1.isFavorite() && o2.isFavorite()) {
                return 1;
            } else if (o1.isRecent() && !o2.isRecent()) {
                return -1;
            } else if (!o1.isRecent() && o2.isRecent()) {
                return 1;
            } else {
                return o1.name().compareTo(o2.name());
            }
        });
    }

    @Override
    public void init() {
        super.init();
        updateList();
        int scrollSize = Math.max(0, favorites.size() - 10);
        addRenderableWidget(new VerticalSlider(bookLeft + 265, bookTop + 46, scrollSize, 1, 1, count -> {
            this.scroll = count;
            updateList();
        }));

        ResourceLocation unchecked = new ResourceLocation(Constants.MOD_ID, "textures/gui/container_filter_unchecked.png");
        ResourceLocation checked = new ResourceLocation(Constants.MOD_ID, "textures/gui/container_filter_checked.png");
        addRenderableWidget(new ToggleImageButton(bookLeft + 206, bookTop + 29, 7, 7, unchecked, checked, b -> {
            this.showBuilds = !this.showBuilds;
            queryFavorites();
        }, () -> this.showBuilds).withTooltip(Component.translatable("blockprints.filter_own_builds")));

        addRenderableWidget(new ToggleImageButton(bookLeft + 224, bookTop + 29, 7, 7, unchecked, checked, b -> {
            this.showFavorites = !this.showFavorites;
            queryFavorites();
        }, () -> this.showFavorites).withTooltip(Component.translatable("blockprints.filter_favorites")));

        addRenderableWidget(new ToggleImageButton(bookLeft + 244, bookTop + 29, 7, 7, unchecked, checked, b -> {
            this.showRecent = !this.showRecent;
            queryFavorites();
        }, () -> this.showRecent).withTooltip(Component.translatable("blockprints.filter_recent_builds")));


        addRenderableWidget(new GuiImageButton(bookLeft + 9, bookTop + 9, 15, 15, new ResourceLocation(Constants.MOD_ID, "textures/gui/button_back.png"), b -> {
            Minecraft.getInstance().setScreen(new HomeScreen());
        }));

    }

    public void updateList() {
        for (FavoritesRow row : rows) {
            removeWidget(row);
        }
        rows = new ArrayList<>();
        List<Favorite> sliced = favorites.subList(scroll, Math.min(scroll + 10, favorites.size()));
        for (int i = 0; i < Math.min(sliced.size(), 10); i++) {
            var entry = sliced.get(i);
            FavoritesRow row = new FavoritesRow(bookLeft + 26, bookTop + 44 + (i * 14),  entry, this);
            rows.add(row);
            addRenderableWidget(row);
        }
    }

    public void queryFavorites() {
        Minecraft.getInstance().setScreen(ViewFavoritesScreen.getTransition(showFavorites, showBuilds, showRecent));
    }

    @Override
    public void render(GuiGraphics matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public void drawBackgroundElements(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.drawBackgroundElements(graphics, mouseX, mouseY, partialTicks);
        graphics.blit(new ResourceLocation(Constants.MOD_ID, "textures/gui/container_list_builds.png"), 25, 25, 0, 0, 239, 159, 239, 159);
        graphics.blit(new ResourceLocation(Constants.MOD_ID, "textures/gui/icon_my_builds.png"), 214, 29, 0, 0, 5, 7, 5, 7);

        graphics.blit(new ResourceLocation(Constants.MOD_ID, "textures/gui/icon_favorite_builds.png"), 232, 29, 0, 0, 7, 7, 7, 7);

        graphics.blit(new ResourceLocation(Constants.MOD_ID, "textures/gui/icon_recent_builds.png"), 252, 29, 0, 0, 7, 7, 7, 7);
        GuiUtils.drawOutlinedText(font, graphics, Component.translatable("blockprints.builds").getVisualOrderText(), 30, 29);
    }
}
