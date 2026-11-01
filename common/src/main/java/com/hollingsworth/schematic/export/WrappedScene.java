package com.hollingsworth.schematic.export;

import com.hollingsworth.nuggets.client.scene.CameraSettings;
import com.hollingsworth.nuggets.client.scene.LytSize;
import com.hollingsworth.nuggets.client.scene.Scene;
import com.hollingsworth.schematic.export.level.FakeForwardingServerLevel;
import com.hollingsworth.schematic.export.level.GuidebookLevel;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.SingleThreadedRandomSource;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.Nullable;

public class WrappedScene {
    private final GuidebookLevel level;
    public final Scene scene;

    public WrappedScene(GuidebookLevel level, CameraSettings camera) {
        this.level = level;
        this.scene = new Scene(level::getFilledBlocks, camera);
    }

//    public void placeStructure(Path filePath){
//        try (DataInputStream stream = new DataInputStream(new BufferedInputStream(
//                new GZIPInputStream(Files.newInputStream(filePath, StandardOpenOption.READ))))) {
//            CompoundTag compoundTag = NbtIo.read(stream, new NbtAccounter(0x20000000L));
//            var template = new StructureTemplate();
//            var blocks = scene.getLevel().registryAccess().registryOrThrow(Registries.BLOCK).asLookup();
//            template.load(blocks, compoundTag);
//            placeStructure(template);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }

    public void placeStructure(StructureTemplate structureTemplate){
        var random = new SingleThreadedRandomSource(0L);
        var settings = new StructurePlaceSettings();
        settings.setIgnoreEntities(true); // Entities need a server level in structures
        structureTemplate.placeInWorld(new FakeForwardingServerLevel(level), BlockPos.ZERO, BlockPos.ZERO, settings, random, 0);
    }

    public ImageExport exportAsPng(float scale) {
        var prefSize = scene.getPreferredSize();
        var scaledSize = getSizeForExport(scale);
        if(scaledSize == null){
            return null;
        }
        return exportAsPng(scaledSize.width(), scaledSize.height(), prefSize);
    }

    public ImageExport exportPreviewPng(){
        LytSize rescaledSize = getSizeForPreview();
        if(rescaledSize == null){
            return null;
        }
        return exportAsPng(rescaledSize.width(), rescaledSize.height(), scene.getPreferredSize());
    }

    public ImageExport exportAsPng(int width, int height, LytSize viewportSize){
        try (var osr = new OffScreenRenderer(width, height)) {
            byte[] image = osr.captureAsPng(() -> {
                var renderer = GuidebookLevelRenderer.getInstance();
                scene.getCameraSettings().setViewportSize(viewportSize);
                renderer.render(level, scene.getCameraSettings());
            });
            return new ImageExport(new LytSize(width, height), image);
        }
    }

    public @Nullable LytSize getSizeForExport(float scale){
        var prefSize = scene.getPreferredSize();
        if (prefSize.width() <= 0 || prefSize.height() <= 0) {
            return null;
        }

        // We only scale the viewport, not scaling the view matrix means the scene will still fill it
        var width = (int) Math.max(1, prefSize.width() * scale);
        var height = (int) Math.max(1, prefSize.height() * scale);
        return new LytSize(width, height);
    }

    public @Nullable LytSize getSizeForPreview(){
        var prefSize = scene.getPreferredSize();
        if (prefSize.width() <= 0 || prefSize.height() <= 0) {
            return null;
        }

        // Make a width and height where the max width is 400 and the max height is the same aspect ratio
        var maxWidth = 400;
        var originalWidth = prefSize.width();
        var originalHeight = prefSize.height();

        double scaleFactor = (double) maxWidth / originalWidth;
        int newHeight = (int) (originalHeight * scaleFactor);
        int newWidth = (int) (originalWidth * scaleFactor);
        return new LytSize(newWidth, newHeight);
    }

    public NativeImage asNativeImage(float scale){
//        if(scene == null){
//            return null;
//        }
//        var prefSize = viewport.getPreferredSize();
//        if (prefSize.width() <= 0 || prefSize.height() <= 0) {
//            return null;
//        }
//
//        // We only scale the viewport, not scaling the view matrix means the scene will still fill it
//        var width = (int) Math.max(1, prefSize.width() * scale);
//        var height = (int) Math.max(1, prefSize.height() * scale);
//        byte[] bytes;
//        try (var osr = new OffScreenRenderer(width, height)) {
//            bytes = osr.captureAsPng(() -> {
//                var renderer = GuidebookLevelRenderer.getInstance();
//                scene.getCameraSettings().setViewportSize(prefSize);
//                renderer.render(scene.getLevel(), scene.getCameraSettings());
//            });
//            ByteBuffer buffer = MemoryUtil.memAlloc(bytes.length);
//            buffer.put(bytes);
//            return NativeImage.read(buffer.flip());
//        } catch (IOException e) {
//            e.printStackTrace();
////            throw new RuntimeException(e);
//        }
        return null;
    }

    public record ImageExport(LytSize size, byte[] image){}
}
