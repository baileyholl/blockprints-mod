package com.hollingsworth.schematic.client.gui;

import com.hollingsworth.schematic.Constants;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class BaseSchematicScreen extends ModScreen{
    public static ResourceLocation background = new ResourceLocation(Constants.MOD_ID, "textures/gui/background.png");
    public BaseSchematicScreen() {
        super(305, 209);
    }

    @Override
    public void init() {
        super.init();
        addRenderableWidget(new GuiImageButton(bookRight - 23, bookBottom - 23, 15, 15, new ResourceLocation(Constants.MOD_ID, "textures/gui/button_discord.png"), b ->{

        }).withTooltip(Component.translatable("blockprints.discord")));
        addRenderableWidget(new GuiImageButton(bookRight - 39, bookBottom - 23, 15, 15, new ResourceLocation(Constants.MOD_ID, "textures/gui/button_website.png"), b ->{

        }).withTooltip(Component.translatable("blockprints.website")));
    }

    @Override
    public ResourceLocation getBgTexture() {
        return background;
    }
}
