package com.hollingsworth.schematic.client.gui;

import com.hollingsworth.schematic.Constants;
import com.hollingsworth.schematic.api.SchematicImporter;
import com.hollingsworth.schematic.client.ClientData;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class EnterCodeScreen extends BaseSchematicScreen {
    public ShortTextField codeField;
    public GuiImageButton submitButton;
    public boolean isDownloading = false;

    public EnterCodeScreen() {
        super();
    }

    @Override
    public void onClose() {
        super.onClose();
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return !isDownloading;
    }

    @Override
    public void init() {
        super.init();
        codeField = new ShortTextField(font, bookLeft + 73, bookTop + 25, Component.empty());
        codeField.setMaxLength(100);
        codeField.setValue("09703653-3f4c-4679-95be-5be71378a40a");
        addRenderableWidget(codeField);
        submitButton = new GuiImageButton(bookLeft + 73, bookTop + 105, 159, 63, new ResourceLocation(Constants.MOD_ID, "textures/gui/button_small.png"), b -> {
            isDownloading = true;
            SchematicImporter.downloadPreview(codeField.getValue()).whenCompleteAsync((result, error) -> {
                isDownloading = false;
                ClientData.setStatus(Component.empty());
                if (result != null) {
                    Minecraft.getInstance().setScreen(new DownloadScreen(result));
                }
            }, Minecraft.getInstance());
        });
        addRenderableWidget(submitButton);
    }
}
