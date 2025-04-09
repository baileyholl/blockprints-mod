package com.hollingsworth.schematic.client.gui;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hollingsworth.schematic.Constants;
import com.hollingsworth.schematic.api.blockprints.BlockprintsApi;
import com.hollingsworth.schematic.api.blockprints.download.PreviewDownloadResult;
import com.hollingsworth.schematic.client.ClientData;
import com.hollingsworth.schematic.client.renderer.StatePos;
import com.hollingsworth.schematic.common.util.BPStructureTemplate;
import com.hollingsworth.schematic.common.util.ClientUtil;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

                String structureJson = """
{
  "blocks": [
    {
      "command": "filled_circle",
      "id": "minecraft:oak_planks",
      "startingPos": [0, 0, 0],
      "radius": 5,
      "height": 1
    },
    {
      "command": "hollow_circle",
      "id": "minecraft:stone",
      "startingPos": [0, 1, 0],
      "radius": 5,
      "height": 8
    },
    {
      "command": "filled_circle",
      "id": "minecraft:oak_planks",
      "startingPos": [0, 8, 0],
      "radius": 5,
      "height": 1
    },
    {
      "id": "minecraft:air",
      "startingPos": [0, 1, -5],
      "endingPos": [1, 3, -5]
    },
    {
      "id": "minecraft:ladder",
      "pos": [4, 1, 0]
    },
    {
      "id": "minecraft:ladder",
      "pos": [4, 2, 0]
    },
    {
      "id": "minecraft:ladder",
      "pos": [4, 3, 0]
    },
    {
      "id": "minecraft:ladder",
      "pos": [4, 4, 0]
    },
    {
      "id": "minecraft:ladder",
      "pos": [4, 5, 0]
    },
    {
      "id": "minecraft:ladder",
      "pos": [4, 6, 0]
    },
    {
      "id": "minecraft:ladder",
      "pos": [4, 7, 0]
    },
    {
      "command": "hollow_circle",
      "id": "minecraft:stone",
      "startingPos": [0, 9, 0],
      "radius": 3,
      "height": 4
    },
    {
      "id": "minecraft:air",
      "pos": [3, 8, 0]
    },
    {
      "id": "minecraft:ladder",
      "pos": [3, 9, 0]
    },
    {
      "id": "minecraft:ladder",
      "pos": [3, 10, 0]
    },
    {
      "id": "minecraft:ladder",
      "pos": [3, 11, 0]
    },
    {
      "command": "filled_circle",
      "id": "minecraft:oak_planks",
      "startingPos": [0, 12, 0],
      "radius": 3,
      "height": 1
    },
    {
      "command": "filled_circle",
      "id": "minecraft:stone",
      "startingPos": [0, 13, 0],
      "radius": 3,
      "height": 1
    },
    {
      "command": "filled_circle",
      "id": "minecraft:stone",
      "startingPos": [0, 14, 0],
      "radius": 2,
      "height": 1
    },
    {
      "command": "filled_circle",
      "id": "minecraft:stone",
      "startingPos": [0, 15, 0],
      "radius": 1,
      "height": 1
    }
  ]
}

""";
                try{
                    JsonObject element = JsonParser.parseString(structureJson).getAsJsonObject();
                    JsonArray blocks = element.getAsJsonArray("blocks");
                    ArrayList<StatePos> posList = new ArrayList<>();
                    for(JsonElement element1 : blocks){
                        JsonObject object = element1.getAsJsonObject();
                        ResourceLocation resourcelocation = ResourceLocation.parse(object.get("id").getAsString());
                        Optional<? extends Holder<Block>> optional = BuiltInRegistries.BLOCK.asLookup().get(ResourceKey.create(Registries.BLOCK, resourcelocation));
                        BlockState state = optional.orElseThrow().value().defaultBlockState();

                        if(object.has("command")){
                            JsonArray position = object.getAsJsonArray("startingPos");
                            BlockPos pos = new BlockPos(position.get(0).getAsInt(), position.get(1).getAsInt(), position.get(2).getAsInt());
                            if(object.get("command").getAsString().equals("hollow_circle")) {
                                for (BlockPos pos1 : generateCircle(pos, object.get("radius").getAsInt(), object.get("height").getAsInt())) {
                                    posList.add(new StatePos(state, pos1));
                                }
                            }else if(object.get("command").getAsString().equals("fill")){
                                BlockPos start = new BlockPos(position.get(0).getAsInt(), position.get(1).getAsInt(), position.get(2).getAsInt());
                                JsonArray endPos = object.getAsJsonArray("endingPos");
                                BlockPos end = new BlockPos(endPos.get(0).getAsInt(), endPos.get(1).getAsInt(), endPos.get(2).getAsInt());
                                for(BlockPos startPos : BlockPos.betweenClosed(start, end)){
                                    posList.add(new StatePos(state, startPos.immutable()));
                                }
                            }else{
                                for (BlockPos pos1 : generateFilledCircle(pos, object.get("radius").getAsInt(), object.get("height").getAsInt())) {
                                    posList.add(new StatePos(state, pos1));
                                }
                            }
                        } else if(object.has("startingPos")){
                            JsonArray position = object.getAsJsonArray("startingPos");
                            BlockPos start = new BlockPos(position.get(0).getAsInt(), position.get(1).getAsInt(), position.get(2).getAsInt());
                            JsonArray endPos = object.getAsJsonArray("endingPos");
                            BlockPos end = new BlockPos(endPos.get(0).getAsInt(), endPos.get(1).getAsInt(), endPos.get(2).getAsInt());
                            BlockPos.betweenClosed(start, end).forEach(pos -> {
                                posList.add(new StatePos(state, pos.immutable()));
                            });
                        }else {
                            JsonArray position = object.getAsJsonArray("pos");
                            BlockPos pos = new BlockPos(position.get(0).getAsInt(), position.get(1).getAsInt(), position.get(2).getAsInt());
                            posList.add(new StatePos(state, pos));
                            System.out.println(new StatePos(state, pos));
                        }
                    }
                    System.out.println(element.get("blocks"));
                    BPStructureTemplate structureTemplate = new BPStructureTemplate(posList);
                    ClientData.startStructureRenderer(structureTemplate, preview.downloadResponse.structureName, preview.downloadResponse.id);
                    Minecraft.getInstance().setScreen(null);
                }catch (Exception e){
                    e.printStackTrace();
                }
//                StructureTemplate structureTemplate = FileUtils.loadStructureTemplate(Minecraft.getInstance().level.holderLookup(Registries.BLOCK), templatePath);
//                var accessor = (StructureTemplateAccessor)structureTemplate;
//                var palettes = accessor.getPalettes();
//                if(palettes.isEmpty()){
//                    Minecraft.getInstance().player.sendSystemMessage(Component.translatable(Constants.MOD_ID + ".invalid_file"));
//                }else {
//                    ClientData.startStructureRenderer(structureTemplate, preview.downloadResponse.structureName, preview.downloadResponse.id);
//                }
//                Minecraft.getInstance().setScreen(null);
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

    public static List<BlockPos> generateCircle(BlockPos center, int radius, int height) {
        List<BlockPos> positions = new ArrayList<>();

        int cx = center.getX();
        int cy = center.getY();
        int cz = center.getZ();

        float outer = radius + 0.5f;
        float inner = radius - 0.5f;
        float outerSq = outer * outer;
        float innerSq = inner * inner;
        for(int y = 0; y < height; y++){
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    float distSq = x * x + z * z;

                    if (distSq >= innerSq && distSq <= outerSq) {
                        positions.add(new BlockPos(cx + x, cy + y, cz + z));
                    }
                }
            }
        }
        return positions;
    }


    public static List<BlockPos> generateFilledCircle(BlockPos center, int radius, int height) {
        List<BlockPos> positions = new ArrayList<>();

        int cx = center.getX();
        int cy = center.getY();
        int cz = center.getZ();

        int radiusSquared = radius * radius;
        for(int y = 0; y < height; y++){
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    if (x * x + z * z <= radiusSquared) {
                        positions.add(new BlockPos(cx + x, cy + y, cz + z));
                    }
                }
            }
        }

        return positions;
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
