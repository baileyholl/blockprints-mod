package com.hollingsworth.schematic.client.gui;

import com.hollingsworth.schematic.Constants;
import com.hollingsworth.schematic.api.blockprints.BlockprintsApi;
import com.hollingsworth.schematic.api.blockprints.download.PreviewDownloadResult;
import com.hollingsworth.schematic.client.ClientData;
import com.hollingsworth.schematic.common.util.ClientUtil;
import com.hollingsworth.schematic.common.util.FileUtils;
import com.hollingsworth.schematic.mixin.StructureTemplateAccessor;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static com.hollingsworth.schematic.api.SceneExporter.STRUCTURE_FOLDER;
import static com.hollingsworth.schematic.api.SceneExporter.sanitize;

public class DownloadScreen extends BaseSchematicScreen {
    public static final ResourceLocation PREVIEW_TEXTURE = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "download_preview");
    DynamicTexture dynamicTexture;
    PreviewDownloadResult preview;
    Screen previousScreen;
    List<BlockListEntry> entries = new ArrayList<>();
    boolean hasMissing = false;

    public DownloadScreen(Screen previousScreen, PreviewDownloadResult preview) {
        super();
        this.previousScreen = previousScreen;
        this.preview = preview;

        for (var entry : preview.downloadResponse.blockCounts) {
            ResourceLocation resourceLocation = ResourceLocation.parse(entry.getA().toString());
            int count = entry.getB();
            boolean exists = BuiltInRegistries.BLOCK.containsKey(resourceLocation);
            Block thing = BuiltInRegistries.BLOCK.get(resourceLocation);
            if (!exists) {
                entries.add(new BlockListEntry(resourceLocation.toString(), count));
                hasMissing = true;
            } else {
                ItemStack renderStack = new ItemStack(thing);
                entries.add(new BlockListEntry(renderStack.getHoverName().getString(), entry.getB(), renderStack));
            }
        }
    }

    public static LoadingScreen<PreviewDownloadResult> getTransition(String schematicId, Screen previousScreen) {
        return new LoadingScreen<>(() -> BlockprintsApi.getInstance().download().downloadPreview(schematicId),
                (result) -> Minecraft.getInstance().setScreen(new DownloadScreen(previousScreen, result)), previousScreen);
    }

    public static DynamicTexture getTexture(byte[] img) throws IOException {
        ByteBuffer buffer = MemoryUtil.memAlloc(img.length);
        buffer.put(img);
        NativeImage nativeImage = NativeImage.read(buffer.flip());
        return new DynamicTexture(nativeImage);
    }

    @Override
    public void init() {
        super.init();
        try {
            dynamicTexture = getTexture(preview.image);
            Minecraft.getInstance().getTextureManager().register(PREVIEW_TEXTURE, dynamicTexture);
            addRenderableWidget(new PreviewImage(bookLeft + 25, bookTop + 41, 100, 100, dynamicTexture, PREVIEW_TEXTURE));
        } catch (Exception e) {
            e.printStackTrace();
        }

        var downloadButton = new GuiImageButton(bookRight - 119, bookTop + 153, 95, 15, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/button_6.png"), b -> {
            startDownload(path ->{
                Minecraft.getInstance().setScreen(null);
                if (path != null) {
                    ClientUtil.sendMessage("blockprints.download_success");
                } else {
                    ClientUtil.sendMessage("blockprints.download_failed");
                }
            });
        });
        var fileName = sanitize(preview.downloadResponse.structureName + "_" + preview.downloadResponse.id) +".nbt";
        var path = Paths.get(STRUCTURE_FOLDER, fileName);
        var alreadyDownloaded = Files.exists(path);
        addRenderableWidget(new GuiImageButton(bookLeft + 25, bookTop + 153, 143, 15, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/button_9.png"), b -> {
            Minecraft.getInstance().setScreen(new BlockListScreen(this, entries));
        }));
        var visualizeButton = new GuiImageButton(bookLeft + 25, bookTop + 153 + 16, 143, 15, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/button_9.png"), b -> {
            Consumer<Path> onPath = (templatePath) ->{
                StructureTemplate structureTemplate = FileUtils.loadStructureTemplate(Minecraft.getInstance().level.holderLookup(Registries.BLOCK), templatePath);
                var accessor = (StructureTemplateAccessor)structureTemplate;
                var palettes = accessor.getPalettes();
                if(palettes.isEmpty()){
                    Minecraft.getInstance().player.sendSystemMessage(Component.translatable(Constants.MOD_ID + ".invalid_file"));
                }else {
                    ClientData.startStructureRenderer(structureTemplate, preview.downloadResponse.structureName, preview.downloadResponse.id);
                }
                Minecraft.getInstance().setScreen(null);
            };

            if(!alreadyDownloaded){
                startDownload(onPath);
            }else{
                onPath.accept(path);
            }
        });

        if(alreadyDownloaded){
            downloadButton.withTooltip(Component.translatable("blockprints.already_downloaded_tooltip", STRUCTURE_FOLDER + fileName).withStyle(Style.EMPTY.withColor(ChatFormatting.RED)));
        }else{
            downloadButton.withTooltip(hasMissing ? Component.translatable("blockprints.blocks_missing_tooltip").withStyle(Style.EMPTY.withColor(ChatFormatting.RED)) : null)
                    .withTooltip(Component.translatable("blockprints.download_tooltip"));
            visualizeButton.withTooltip(Component.translatable("blockprints.visualize_download"));
        }

        addRenderableWidget(visualizeButton);
        addRenderableWidget(downloadButton);
        addRenderableWidget(new GuiImageButton(bookLeft + 9, bookTop + 9, 15, 15, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/button_back.png"), b -> {
            Minecraft.getInstance().setScreen(previousScreen);
        }));
    }

    public void startDownload(Consumer<Path> callback){
        Minecraft.getInstance().setScreen(new LoadingScreen<>(() -> BlockprintsApi.getInstance().download().downloadSchematic(preview.downloadResponse.id, preview.downloadResponse.structureName), callback));
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        graphics.blit(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/icon_download.png"), bookRight - 116, bookTop + 155, 0, 0, 9, 11, 9, 11);
        graphics.blit(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/icon_list.png"), bookLeft + 28, bookTop + 157, 0, 0, 9, 7, 9, 7);
        graphics.blit(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/icon_visualize.png"), bookLeft + 28, bookTop + 157 + 16, 0, 0, 9, 7, 9, 7);
        GuiUtils.drawCenteredOutlinedText(font, graphics, Component.translatable("blockprints.download").getVisualOrderText(), bookRight - 67, bookTop + 157);
        GuiUtils.drawCenteredOutlinedText(font, graphics, Component.translatable("blockprints.view_list").getVisualOrderText(), bookLeft + 34 + 143 / 2, bookTop + 157);
        GuiUtils.drawCenteredOutlinedText(font, graphics, Component.translatable("blockprints.visualize").getVisualOrderText(), bookLeft + 34 + 143 / 2, bookTop + 157 + 16);
    }

    @Override
    public void drawBackgroundElements(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.drawBackgroundElements(graphics, mouseX, mouseY, partialTicks);
        graphics.blit(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/diologue_preview.png"), 25, 25, 0, 0, 143, 127, 143, 127);
        graphics.blit(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/diologue_title_body.png"), 185, 25, 0, 0, 95, 127, 95, 127);
        GuiUtils.drawCenteredOutlinedText(font, graphics, Component.literal(preview.downloadResponse.structureName), 25 + 143 / 2, 29);
        graphics.drawWordWrap(font, Component.literal(preview.downloadResponse.description), 187, 44, 95, 0);
        GuiUtils.drawCenteredOutlinedText(font, graphics, Component.translatable("blockprints.description_title"), 185 + 94 / 2, 29);
    }

    @Override
    public void removed() {
        super.removed();
        Minecraft.getInstance().getTextureManager().release(PREVIEW_TEXTURE);
    }

    public static class BlockListEntry {
        public String name;
        public int count;
        public ItemStack renderStack;
        public boolean isMissing;

        public BlockListEntry(String name, int count, ItemStack renderStack) {
            this.name = name;
            this.count = count;
            this.renderStack = renderStack;
            this.isMissing = false;
        }

        public BlockListEntry(String name, int count) {
            this(name, count, ItemStack.EMPTY);
            this.isMissing = true;
        }

    }
}
