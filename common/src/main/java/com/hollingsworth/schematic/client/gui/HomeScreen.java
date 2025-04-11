package com.hollingsworth.schematic.client.gui;

import com.hollingsworth.schematic.Constants;
import com.hollingsworth.schematic.client.ClientData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class HomeScreen extends BaseSchematicScreen {
    public HomeScreen() {
        super();
    }

    @Override
    public void init() {
        super.init();

        addRenderableWidget(new GuiImageButton(bookLeft + 41, bookTop + 41, 223, 47, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/button_navigation.png"), b -> {
            requireTokenOrLogin(() ->{
                ClientData.startBoundaryCapture();
                Minecraft.getInstance().setScreen(null);
                Minecraft.getInstance().player.sendSystemMessage(Component.translatable(Constants.MOD_ID + ".start_selecting"));
            });
        }));
        addRenderableWidget(new GuiImageButton(bookLeft + 41, bookTop + 89, 223, 47, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/button_navigation.png"), b -> {
            Minecraft.getInstance().setScreen(new EnterCodeScreen());
        }));

        addRenderableWidget(new GuiImageButton(bookLeft + 41, bookTop + 137, 223, 47, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/button_navigation.png"), b -> {
            requireTokenOrLogin(() -> {
                Minecraft.getInstance().setScreen(ViewFavoritesScreen.getTransition());
            });
        }));
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        graphics.blit(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/logo_container.png"), bookLeft + 5, bookTop + 7, 0, 0, 295, 24, 295, 24);
        graphics.blit(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/logo_blockprints.png"), bookLeft + 105, bookTop + 9, 0, 0, 96, 16, 96, 16);


        graphics.blit(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/icon_upload_large.png"), bookLeft + 51, bookTop + 48, 0, 0, 27, 33, 27, 33);

        graphics.blit(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/icon_download_large.png"), bookLeft + 51, bookTop + 96, 0, 0, 27, 33, 27, 33);
        graphics.blit(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/icon_build_list_large.png"), bookLeft + 49, bookTop + 144, 0, 0, 33, 33, 33, 33);
        GuiUtils.drawOutlinedText(font, graphics, Component.translatable("blockprints.upload").getVisualOrderText(), bookLeft + 91, bookTop + 45);
        GuiUtils.drawOutlinedText(font, graphics, Component.translatable("blockprints.download").getVisualOrderText(), bookLeft + 91, bookTop + 93);
        GuiUtils.drawOutlinedText(font, graphics, Component.translatable("blockprints.build_list").getVisualOrderText(), bookLeft + 91, bookTop + 141);

        graphics.drawWordWrap(font, Component.translatable("blockprints.upload_desc"), bookLeft + 91, bookTop + 61, 150, 0);
        graphics.drawWordWrap(font, Component.translatable("blockprints.download_desc"), bookLeft + 91, bookTop + 108, 150, 0);
        graphics.drawWordWrap(font, Component.translatable("blockprints.build_list_desc"), bookLeft + 91, bookTop + 156, 150, 0);
    }

    @Override
    public void drawForegroundElements(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.drawForegroundElements(graphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public void drawBackgroundElements(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.drawBackgroundElements(graphics, mouseX, mouseY, partialTicks);
    }
}
