package com.hollingsworth.schematic.api;

import com.hollingsworth.schematic.client.ClientData;
import com.hollingsworth.schematic.export.PerspectivePreset;
import com.hollingsworth.schematic.export.WrappedScene;
import net.minecraft.Util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;


public class SceneExporter {
    private static final int GAMESCENE_PLACEHOLDER_SCALE = 2;
    public WrappedScene scene;

    public SceneExporter(WrappedScene wrappedScene){
        this.scene = wrappedScene;
    }

    public void exportLocally(String exportName) throws IOException {
//        addPlaceholder(scene, exportName);
        byte[] selectedImage = scene.exportAsPng(GAMESCENE_PLACEHOLDER_SCALE);
        List<byte[]> images = new ArrayList<>();
        images.add(selectedImage);
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
                    for(byte[] image : images) {
                        ClientData.uploadStatus.set("Uploading " + exportName + count + ".png");
                        Files.createDirectories(Paths.get("./schematics/blockprints/images/"));
                        Files.write(Paths.get("./schematics/blockprints/images/" + exportName + count++ +".png"), image);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, Util.backgroundExecutor()).whenComplete((t, a) ->{
                System.out.println("done");
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
        byte[] imageContent = scene.exportAsPng(GAMESCENE_PLACEHOLDER_SCALE);
        if (imageContent != null) {
//            Files.createDirectories(Paths.get("./schematics"));
            Files.write(imagePath, imageContent, StandardOpenOption.CREATE);
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
