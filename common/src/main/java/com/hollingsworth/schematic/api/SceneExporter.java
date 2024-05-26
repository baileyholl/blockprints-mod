package com.hollingsworth.schematic.api;

import com.hollingsworth.schematic.api.blockprints.ApiResponse;
import com.hollingsworth.schematic.api.blockprints.GoogleCloudStorage;
import com.hollingsworth.schematic.api.blockprints.upload.Upload;
import com.hollingsworth.schematic.common.util.SchematicExport;
import com.hollingsworth.schematic.export.PerspectivePreset;
import com.hollingsworth.schematic.export.WrappedScene;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public class SceneExporter {
    public static final String IMAGE_FOLDER = "./schematics/blockprints/images/";
    public static final String STRUCTURE_FOLDER = "./schematics/";

    public static final int GAMESCENE_PLACEHOLDER_SCALE = 2;
    public WrappedScene scene;
    public StructureTemplate structureTemplate;

    public SceneExporter(WrappedScene wrappedScene, StructureTemplate structureTemplate) {
        this.scene = wrappedScene;
        this.structureTemplate = structureTemplate;
    }

    public List<WrappedScene.ImageExport> getImages() {
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
        for (PerspectivePreset preset : perspectivePresets) {
            if (preset != closest) {
                scene.scene.getCameraSettings().setPerspectivePreset(preset);
                images.add(scene.exportAsPng(GAMESCENE_PLACEHOLDER_SCALE));
            }
        }
        return images;
    }

    public ApiResponse<Boolean> writeAndUpload(List<WrappedScene.ImageExport> images, String name, String description, boolean makePublic, BlockPos start, BlockPos end) {
        String finalExportName = sanitize(name);
        try {
            Files.createDirectories(Path.of(IMAGE_FOLDER));
            int count = 0;
            List<Path> imageFiles = new ArrayList<>();
            // Skip the first image
            Path previewPath = Paths.get(IMAGE_FOLDER + finalExportName + "_preview.png");
            Files.write(previewPath, images.get(0).image());
            List<WrappedScene.ImageExport> galleryImages = images.subList(1, images.size());

            for (WrappedScene.ImageExport image : galleryImages) {
                Path path = Paths.get(IMAGE_FOLDER + finalExportName + count + ".png");
                Files.write(path, image.image());
                imageFiles.add(path);
                count++;
            }
            var uploadResponse = Upload.postUpload(name, description, makePublic);
            if (!uploadResponse.wasSuccessful() || uploadResponse.response == null) {
                return ApiResponse.unexpectedFailure();
            }
            var response = uploadResponse.response;
            String localStructureName = sanitize(finalExportName + '_' + response.id);
            SchematicExport.SchematicExportResult result = SchematicExport.saveSchematic(Paths.get(STRUCTURE_FOLDER), localStructureName, false, structureTemplate, start, end);

            if(result == null){
                return ApiResponse.unexpectedFailure();
            }

            var preview = response.signedPreviewImage;
            var schematic = response.signedSchematic;
            var previewSuccess = GoogleCloudStorage.uploadFileToGCS(URI.create(preview).toURL(), previewPath, "image/png", response.imageFileSize);
            var structureSuccess = GoogleCloudStorage.uploadFileToGCS(URI.create(schematic).toURL(), result.file(), "application/octet-stream", response.schematicFileSize);
            if (!previewSuccess || !structureSuccess) {
                return ApiResponse.unexpectedFailure();
            }
            for (int i = 0; i < response.signedImages.length; i++) {
                // Guard against server changes
                if (i >= imageFiles.size()) {
                    break;
                }
                if (!GoogleCloudStorage.uploadFileToGCS(URI.create(response.signedImages[i]).toURL(), imageFiles.get(i), "image/png", response.imageFileSize)) {
                    return ApiResponse.unexpectedFailure();
                }
            }
            return Upload.postDoneUploading(response.id);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return ApiResponse.connectionError();
        }
    }

    // Convert a string into lowercase, remove all non-alphanumeric characters, and replace all spaces with underscores
    public static String sanitize(String str) {
        return str.toLowerCase().replaceAll("[^a-z0-9]", "_").replaceAll(" ", "_");
    }
}
