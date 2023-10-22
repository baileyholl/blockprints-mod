package com.hollingsworth.schematic.api;

import com.hollingsworth.schematic.api.blockprints.GoogleCloudStorage;
import com.hollingsworth.schematic.api.blockprints.Upload;
import com.hollingsworth.schematic.client.ClientData;
import com.hollingsworth.schematic.common.util.ClientUtil;
import com.hollingsworth.schematic.common.util.SchematicExport;
import com.hollingsworth.schematic.export.PerspectivePreset;
import com.hollingsworth.schematic.export.WrappedScene;
import net.minecraft.Util;
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
    public static final String IMAGE_FOLDER = "./schematics/blockprints/images/";
    public static final String STRUCTURE_FOLDER = "./schematics/blockprints/structures/";

    private static final int GAMESCENE_PLACEHOLDER_SCALE = 2;
    public WrappedScene scene;
    public StructureTemplate structureTemplate;
    public SceneExporter(WrappedScene wrappedScene, StructureTemplate structureTemplate){
        this.scene = wrappedScene;
        this.structureTemplate = structureTemplate;
    }

    public List<WrappedScene.ImageExport> getImages(){
        List<WrappedScene.ImageExport> images = new ArrayList<>();
        images.add(scene.exportPreviewPng());
        images.add(scene.exportAsPng(GAMESCENE_PLACEHOLDER_SCALE));
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
        return images;
    }

    public void exportLocally(String name, String description) throws IOException {
        List<WrappedScene.ImageExport> images = getImages();


        String finalExportName = sanitize(name);
        CompletableFuture.runAsync(() -> {
            try {
                int count = 0;
                List<Path> imageFiles = new ArrayList<>();
                // Skip the first image
                Path previewPath = Paths.get(IMAGE_FOLDER + finalExportName + "_preview.png");
                Files.write(previewPath, images.get(0).image());
                List<WrappedScene.ImageExport> galleryImages = images.subList(1, images.size());

                for (WrappedScene.ImageExport image : galleryImages) {
                    ClientData.uploadStatus.set(Component.translatable("blockprints.saving", finalExportName + count + ".png").getString());
                    Files.createDirectories(Paths.get(IMAGE_FOLDER));
                    Path path = Paths.get(IMAGE_FOLDER + finalExportName + count + ".png");
                    Files.write(path, image.image());
                    imageFiles.add(path);
                    count++;
                }
                SchematicExport.SchematicExportResult result = SchematicExport.saveSchematic(Paths.get(STRUCTURE_FOLDER), finalExportName, false, structureTemplate);
                var response = Upload.postUpload(name, description);
                if (response == null) {
                    ClientUtil.sendMessage( "blockprints.cannot_contact");
                }
                var preview = response.signedPreviewImage;
                var schematic = response.signedSchematic;
                ClientData.uploadStatus.set(Component.translatable("blockprints.uploading").getString());
                var previewSuccess = GoogleCloudStorage.uploadFileToGCS(URI.create(preview).toURL(), previewPath, "image/png", response.imageFileSize);
                var structureSuccess = GoogleCloudStorage.uploadFileToGCS(URI.create(schematic).toURL(), result.file(), "application/octet-stream", response.schematicFileSize);
                if(!previewSuccess || !structureSuccess){
                    ClientUtil.sendMessage("blockprints.upload_failed");
                    return;
                }
                for (int i = 0; i < response.signedImages.length; i++) {
                    // Guard against server changes
                    if (i >= imageFiles.size()) {
                        break;
                    }
                    if(!GoogleCloudStorage.uploadFileToGCS(URI.create(response.signedImages[i]).toURL(), imageFiles.get(i), "image/png", response.imageFileSize)){
                        ClientUtil.sendMessage("blockprints.upload_failed");
                        return;
                    }
                }
                ClientData.uploadStatus.set(Component.translatable("blockprints.confirming").getString());
                if(Upload.postDoneUploading(response.id)) {
                    ClientUtil.sendMessage("blockprints.upload_complete");
                }else{
                    ClientUtil.sendMessage("blockprints.upload_failed");
                }
            }catch (Exception e){
                e.printStackTrace();
                ClientUtil.sendMessage(Component.translatable("blockprints.upload_failed"));
            }
        }, Util.backgroundExecutor()).whenComplete((t, a) ->{
            ClientData.uploadStatus.set("");
        });
    }

    // Convert a string into lowercase, remove all non-alphanumeric characters, and replace all spaces with underscores
    public static String sanitize(String str) {
        return str.toLowerCase().replaceAll("[^a-z0-9]", "_").replaceAll(" ", "_");
    }
}
