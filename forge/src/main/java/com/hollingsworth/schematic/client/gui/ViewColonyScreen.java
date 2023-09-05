package com.hollingsworth.schematic.client.gui;

import com.hollingsworth.schematic.Constants;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.UUID;

public class ViewColonyScreen extends ModScreen{
    public static ResourceLocation background = new ResourceLocation(Constants.MOD_ID, "textures/gui/spell_book_template.png");

    List<ItemStack> menuItems;
    int numSeats;
    int numSpawners;
    public Button startGameButton;
    public UUID runningGameID;
    public Button createCafeButton;

    public ViewColonyScreen(List<ItemStack> menuItems, int numSeats, int numSpawners, UUID runningGameID) {
        super(290, 194);
        this.menuItems = menuItems;
        this.numSeats = numSeats;
        this.numSpawners = numSpawners;
        this.runningGameID = runningGameID;
    }

    @Override
    public void init() {
        super.init();
        for(int i = 0; i < Math.min(18, menuItems.size()); i++){
            ItemStack stack = menuItems.get(i);
            HoverableItem item = new HoverableItem(bookLeft + 155 + 20 * (i % 6), bookTop + 30 + (20 * (i / 6)), stack);
            addRenderableWidget(item);
        }
        startGameButton = new ANButton(bookRight - 110, bookBottom - 40, 70, 20, Component.translatable(runningGameID != null ? "cafetier.cancel_game" : "cafetier.start_game"), this::startGame);
        createCafeButton = new ANButton(bookLeft + 40, bookBottom - 40, 70, 20, Component.translatable("cafetier.change_cafe"), this::openCreate);
        addRenderableWidget(createCafeButton);
        addRenderableWidget(startGameButton);
    }

    public void openCreate(Button button){
    }

    public void startGame(Button button){
        minecraft.setScreen(null);
    }

    @Override
    public ResourceLocation getBgTexture() {
        return background;
    }
}
