package com.hollingsworth.schematic.client.gui;

import com.hollingsworth.schematic.Constants;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.net.URI;
import java.net.URISyntaxException;

public class EnterCodeScreen extends BaseSchematicScreen {
    public LongTextField codeField;
    public GuiImageButton submitButton;

    public EnterCodeScreen() {
        super();
    }

    @Override
    public void onClose() {
        super.onClose();
    }

    @Override
    public void init() {
        super.init();
        codeField = new LongTextField(font, bookLeft + 41, bookTop + 119, Component.empty());
        codeField.setMaxLength(100);
        addRenderableWidget(codeField);
        submitButton = new GuiImageButton(bookLeft + 57, bookTop + 153, 95, 15, new ResourceLocation(Constants.MOD_ID, "textures/gui/button_6.png"), b -> {
            Minecraft.getInstance().setScreen(DownloadScreen.getTransition(codeField.getValue().trim(), this));
        });
        addRenderableWidget(new GuiImageButton(bookLeft + 153, bookTop + 153, 95, 15, new ResourceLocation(Constants.MOD_ID, "textures/gui/button_6.png"), b -> {
            try {
                Util.getPlatform().openUri(new URI("https://blockprints.io"));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }));
        addRenderableWidget(new GuiImageButton(bookLeft + 9, bookTop + 9, 15, 15, new ResourceLocation(Constants.MOD_ID, "textures/gui/button_back.png"), b -> {
            Minecraft.getInstance().setScreen(new HomeScreen());
        }));
        addRenderableWidget(submitButton);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        graphics.blit(new ResourceLocation(Constants.MOD_ID, "textures/gui/icon_preview.png"), bookLeft + 59, bookTop + 156, 0, 0, 11, 9, 11, 9);
        graphics.blit(new ResourceLocation(Constants.MOD_ID, "textures/gui/icon_website.png"), bookLeft + 155, bookTop + 155, 0, 0, 11, 11, 11, 11);
        GuiUtils.drawOutlinedText(font, graphics, Component.translatable("blockprints.preview").getVisualOrderText(), bookLeft + 92, bookTop + 157);
        GuiUtils.drawOutlinedText(font, graphics, Component.literal("BlockPrints.io").getVisualOrderText(), bookLeft + 176, bookTop + 157);

        GuiUtils.drawCenteredOutlinedText(font, graphics, Component.translatable("blockprints.enter_code").getVisualOrderText(), width / 2, bookTop + 61);
        graphics.drawWordWrap(font, Component.translatable("blockprints.download_desc"), bookLeft + 44, bookTop + 75, 225, 0);
    }

    @Override
    public void drawBackgroundElements(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.drawBackgroundElements(graphics, mouseX, mouseY, partialTicks);
        graphics.blit(new ResourceLocation(Constants.MOD_ID, "textures/gui/dialog_code_static.png"), 41, 57, 0, 0, 223, 62, 223, 62);
    }
}
