package com.hollingsworth.schematic.api;

import com.hollingsworth.schematic.api.blockprints.GoogleCloudStorage;
import com.hollingsworth.schematic.api.blockprints.Upload;
import com.hollingsworth.schematic.client.ClientData;
import com.hollingsworth.schematic.common.util.SchematicExport;
import com.hollingsworth.schematic.export.PerspectivePreset;
import com.hollingsworth.schematic.export.WrappedScene;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;


public class SceneExporter {
    private static final int GAMESCENE_PLACEHOLDER_SCALE = 2;
    public WrappedScene scene;
    public StructureTemplate structureTemplate;
    public SceneExporter(WrappedScene wrappedScene, StructureTemplate structureTemplate){
        this.scene = wrappedScene;
        this.structureTemplate = structureTemplate;
    }

    public void exportLocally(String exportName) throws IOException {
//        addPlaceholder(scene, exportName);
        WrappedScene.ImageExport selectedImage = scene.exportAsPng(GAMESCENE_PLACEHOLDER_SCALE);
        List<WrappedScene.ImageExport> images = new ArrayList<>();
        images.add(selectedImage);
        WrappedScene.ImageExport smallPreview = scene.exportPreviewPng();
        PerspectivePreset[] perspectivePresets = PerspectivePreset.values();
        // check which preset the scene is closest to
        float currentYaw = scene.scene.getCameraSettings().getRotationY();
        float currentPitch = scene.scene.getCameraSettings().getRotationX();
        float currentRoll = scene.scene.getCameraSettings().getRotationZ();
        PerspectivePreset closest = PerspectivePreset.ISOMETRIC_NORTH_EAST;
        float closestDistance = Float.MAX_VALUE;
        for (PerspectivePreset preset : perspectivePresets) {
            float yaw = preset.yaw();
            float pitch = preset.pitch();
            float roll = preset.roll();
            float distance = (float) Math.sqrt(Math.pow(currentYaw - yaw, 2) + Math.pow(currentPitch - pitch, 2) + Math.pow(currentRoll - roll, 2));
            if (distance < closestDistance) {
                closest = preset;
                closestDistance = distance;
            }
        }
        for(PerspectivePreset preset : perspectivePresets){
            if(preset != closest){
                scene.scene.getCameraSettings().setPerspectivePreset(preset);
                images.add(scene.exportAsPng(GAMESCENE_PLACEHOLDER_SCALE));
            }
        }
        try {
            CompletableFuture.runAsync(() -> {

                try {
                    int count = 0;
                    List<Path> imageFiles = new ArrayList<>();
                    for(WrappedScene.ImageExport image : images) {
                        ClientData.uploadStatus.set("Uploading " + exportName + count + ".png");
                        Files.createDirectories(Paths.get("./schematics/blockprints/images/"));
                        Path path = Paths.get("./schematics/blockprints/images/" + exportName + count +".png");
                        Files.write(path, image.image());
                        imageFiles.add(path);
                        count++;
                    }
                    Path previewPath = Paths.get("./schematics/blockprints/" + exportName + "_preview.png");
                    Files.write(previewPath, smallPreview.image());
                    SchematicExport.SchematicExportResult result = SchematicExport.saveSchematic(Paths.get("./schematics/blockprints/structures/"), exportName, false, structureTemplate);
                    var response = Upload.postUpload("test", "test");
                    var preview = response.signedImages[0];
                    var schematic = response.signedSchematic;
                    ClientData.uploadStatus.set(Component.translatable("blockprints.uploading").getString());
                    GoogleCloudStorage.uploadFileToGCS(URI.create(preview).toURL(), previewPath, "image/png");
                    GoogleCloudStorage.uploadFileToGCS(URI.create(schematic).toURL(), result.file(), "application/octet-stream");
                    for(int i = 1; i < response.signedImages.length; i++){
                        if(i >= imageFiles.size()){
                            break;
                        }
                        GoogleCloudStorage.uploadFileToGCS(URI.create(response.signedImages[i]).toURL(), imageFiles.get(i), "image/png");
                    }
                    if(Minecraft.getInstance().player != null){
                        Minecraft.getInstance().player.sendSystemMessage(Component.translatable("blockprints.upload_complete"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, Util.backgroundExecutor()).whenComplete((t, a) ->{
                ClientData.uploadStatus.set("");
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    // Creates the png
    private void addPlaceholder(WrappedScene scene, String exportName)
            throws IOException {

        // For GameScenes, we create a placeholder PNG to show in place of the WebGL scene
        // while that is still loading.
        var imagePath = Paths.get("./schematics/" + exportName + ".png");
        WrappedScene.ImageExport imageContent = scene.exportAsPng(GAMESCENE_PLACEHOLDER_SCALE);
        if (imageContent != null) {
//            Files.createDirectories(Paths.get("./schematics"));
//            Files.write(imagePath, imageContent, StandardOpenOption.CREATE);
        }
    }

    public void writeFile(Path path, byte[] content) throws IOException {
        Files.createDirectories(path);
        Files.write(path, content);
    }

    // creates the scene file
    private String exportScene(WrappedScene scene, String baseName) throws IOException {
        return "";
//        var scenePath = exporter.getPageSpecificPathForWriting(baseName + ".scene.gz");
//        var exporter = new SceneExporter(this.exporter);
//        var sceneContent = exporter.export(scene.getScene());
//        scenePath = CacheBusting.writeAsset(scenePath, sceneContent);
//
//        return this.exporter.getPathRelativeFromOutputFolder(scenePath);
    }
}
