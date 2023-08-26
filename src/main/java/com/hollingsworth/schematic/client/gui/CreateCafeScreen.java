package com.hollingsworth.schematic.client.gui;

import com.hollingsworth.schematic.SchematicMod;
import com.hollingsworth.schematic.common.network.DownloadSchematic;
import com.hollingsworth.schematic.common.network.Networking;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class CreateCafeScreen extends ModScreen {

    public static ResourceLocation background = new ResourceLocation(SchematicMod.MODID, "textures/gui/spell_book_template.png");

    public EditBox cafeName;
    public Button confirm;

    public CreateCafeScreen() {
        super(290, 194);
    }

    @Override
    public void init() {
        super.init();
        cafeName = new EditBox(font, bookLeft + 50, bookTop + 36, 80, 14, Component.empty());
        confirm = new Button(bookLeft + 40, bookTop + 150, 80, 20, Component.translatable("cafetier.create_schematic"), this::onCreate);
        cafeName.setMaxLength(64);
        addRenderableWidget(cafeName);
        addRenderableWidget(confirm);
    }


    public void onCreate(Button button){
        Networking.sendToServer(new DownloadSchematic(cafeName.getValue()));
        minecraft.setScreen(null);
    }


    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
    }

    @Override
    public void drawBackgroundElements(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.drawBackgroundElements(graphics, mouseX, mouseY, partialTicks);
        int color = -8355712;
        graphics.drawString(font, Component.literal("Structure").getString(), 50, 20, color, false);
        graphics.drawString(font, Component.literal("ID").getString(), 20, 40, color, false);
//        graphics.drawString(font, Component.translatable("cafetier.select_cafe"));

//        graphics.drawString(font, Component.translatable("cafetier.menu").getString(), 200, 20, color, false);
//        graphics.drawString(font, Component.translatable("cafetier.seats", numSeats).getString(), 155, bookTop + 75, color, false);
//        graphics.drawString(font, Component.translatable("cafetier.spawners", numSpawners).getString(), 155, bookTop + 85, color, false);
    }

    @Override
    public ResourceLocation getBgTexture() {
        return background;
    }
}
