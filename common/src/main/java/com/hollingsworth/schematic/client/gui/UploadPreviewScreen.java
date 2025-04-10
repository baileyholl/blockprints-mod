package com.hollingsworth.schematic.client.gui;

import com.hollingsworth.schematic.Constants;
import com.hollingsworth.schematic.api.SceneExporter;
import com.hollingsworth.schematic.common.util.ClientUtil;
import com.hollingsworth.schematic.export.WrappedScene;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.List;

public class UploadPreviewScreen extends BaseSchematicScreen {

    ScenePreview scenePreview;
    ShortTextField nameField;
    NoScrollMultiText descriptionField;
    GuiImageButton uploadButton;
    public StructureTemplate structureTemplate;
    public static final int MAX_NAME_LENGTH = 100;
    public static final int MIN_NAME_LENGTH = 10;
    public static final int MAX_DESC_LENGTH = 1000;
    public static final int MIN_DESC_LENGTH = 20;
    public boolean makePublic = false;
    public BlockPos start;
    public BlockPos end;
    WrappedScene wrappedScene;

    public UploadPreviewScreen(StructureTemplate structureTemplate, BlockPos start, BlockPos end) {
        super();
        this.structureTemplate = structureTemplate;
        this.start = start;
        this.end = end;
        wrappedScene = new WrappedScene();
    }

    @Override
    public void init() {
        super.init();
        wrappedScene.placeStructure(structureTemplate);
        nameField = new ShortTextField(font, bookLeft + 185, bookTop + 39, Component.empty());
        descriptionField = new NoScrollMultiText(font, bookLeft + 185, bookTop + 71, 95, 81, Component.empty(), Component.empty());
        uploadButton = new GuiImageButton(bookRight - 119, bookTop + 169, 95, 15, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/button_6.png"), b -> {
            var name = nameField.getValue().trim();
            var desc = descriptionField.getValue().trim();
            // return if the name or description is too long or too short
            if (name.length() > MAX_NAME_LENGTH || name.length() < MIN_NAME_LENGTH || desc.length() > MAX_DESC_LENGTH || desc.length() < MIN_DESC_LENGTH) {
                return;
            }
            SceneExporter sceneExporter = new SceneExporter(wrappedScene, structureTemplate);
            if(sceneExporter.scene.getSizeForExport(SceneExporter.GAMESCENE_PLACEHOLDER_SCALE) == null){
                Minecraft.getInstance().setScreen(null);
                ClientUtil.sendMessage(Component.translatable("blockprints.invalid_upload").withStyle(Style.EMPTY.withColor(ChatFormatting.RED)));
                return;
            }
            List<WrappedScene.ImageExport> images = sceneExporter.getImages();


            Minecraft.getInstance().setScreen(new LoadingScreen<>(() -> sceneExporter.writeAndUpload(images, this.nameField.getValue(), this.descriptionField.getValue(), this.makePublic, start, end), (res) -> {
                Minecraft.getInstance().setScreen(null);
                var url = "https://blockprints.io/schematic/" + res;
                ClientUtil.sendMessage(Component.translatable("blockprints.upload_complete", Component.literal(url).withStyle(Style.EMPTY.withColor(ChatFormatting.BLUE).withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url)))));
            }));
        });
        addRenderableWidget(uploadButton);
        addRenderableWidget(nameField);
        addRenderableWidget(descriptionField);

        this.scenePreview = new ScenePreview(bookLeft + 25, bookTop + 41, 100, 100, wrappedScene, structureTemplate);

        scenePreview.setYaw(225);
        scenePreview.setPitch(30);
        addRenderableWidget(scenePreview);
        addRenderableWidget(new GuiImageButton(bookLeft + 9, bookTop + 9, 15, 15, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/button_back.png"), b -> {
            Minecraft.getInstance().setScreen(new HomeScreen());
        }));
        addRenderableWidget(new CheckBoxButton(bookRight - 119, bookTop + 153, b -> {
            this.makePublic = !this.makePublic;
        }, () -> this.makePublic).withTooltip(Component.translatable("blockprints.make_public_tooltip")));
    }

    @Override
    public void tick() {
        super.tick();
        scenePreview.tick();
    }

    @Override
    public void removed() {
        super.removed();
        scenePreview.removed();
    }

    @Override
    public void render(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(graphics, pMouseX, pMouseY, pPartialTick);
        graphics.blit(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/icon_upload.png"), bookRight - 116, bookTop + 171, 0, 0, 9, 11, 9, 11);
        GuiUtils.drawCenteredOutlinedText(font, graphics, Component.translatable("blockprints.make_public").getVisualOrderText(), bookRight - 67, bookTop + 157);
        GuiUtils.drawCenteredOutlinedText(font, graphics, Component.translatable("blockprints.upload").getVisualOrderText(), bookRight - 67, bookTop + 173);
    }

    @Override
    public void collectTooltips(GuiGraphics stack, int mouseX, int mouseY, List<Component> tooltip) {
        super.collectTooltips(stack, mouseX, mouseY, tooltip);
        if (GuiUtils.isMouseInRelativeRange(mouseX, mouseY, uploadButton)) {
            var name = nameField.getValue().trim();
            var desc = descriptionField.getValue().trim();
            if (name.length() > MAX_NAME_LENGTH) {
                tooltip.add(Component.translatable("blockprints.name_too_long"));
            }
            if (name.length() < MIN_NAME_LENGTH) {
                tooltip.add(Component.translatable("blockprints.name_too_short"));
            }
            if (desc.length() > MAX_DESC_LENGTH) {
                tooltip.add(Component.translatable("blockprints.description_too_long"));
            }
            if (desc.length() < MIN_DESC_LENGTH) {
                tooltip.add(Component.translatable("blockprints.description_too_short"));
            }
        }
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pScrollX, double pScrollY) {
//        if(GuiUtils.isMouseInRelativeRange(pMouseX, pMouseY, yawSlider)) {
//            return yawSlider.mouseScrolled(pMouseX, pMouseY, pScrollX, pScrollY);
//        }else if(GuiUtils.isMouseInRelativeRange(pMouseX, pMouseY, pitchSlider)){
//            return pitchSlider.mouseScrolled(pMouseX, pMouseY, pScrollX, pScrollY);
//        }
        return super.mouseScrolled(pMouseX, pMouseY, pScrollX, pScrollY);
    }

    @Override
    public void drawBackgroundElements(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.drawBackgroundElements(graphics, mouseX, mouseY, partialTicks);
        graphics.blit(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/diologue_preview.png"), 25, 25, 0, 0, 143, 127, 143, 127);
        graphics.blit(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/diologue_title.png"), 185, 25, 0, 0, 95, 14, 95, 14);
        graphics.blit(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/diologue_title.png"), 185, 57, 0, 0, 95, 14, 95, 14);
        GuiUtils.drawCenteredOutlinedText(font, graphics, Component.translatable("blockprints.set_preview").getVisualOrderText(), 25 + 143 / 2, 29);
        GuiUtils.drawCenteredOutlinedText(font, graphics, Component.translatable("blockprints.name").getVisualOrderText(), 185 + 48, 29);
        GuiUtils.drawCenteredOutlinedText(font, graphics, Component.translatable("blockprints.description").getVisualOrderText(), 185 + 48, 61);
    }
}
