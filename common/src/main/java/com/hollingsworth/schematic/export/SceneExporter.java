package com.hollingsworth.schematic.export;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


public class SceneExporter {
    private static final int GAMESCENE_PLACEHOLDER_SCALE = 2;
    private void handleScene(WrappedScene scene, String tagName) throws IOException {
        var exportName = "scene";
        var relativePath = exportScene(scene, exportName);
        addPlaceholder(scene, exportName);
    }

    // Creates the png
    private void addPlaceholder(WrappedScene scene, String exportName)
            throws IOException {

        // For GameScenes, we create a placeholder PNG to show in place of the WebGL scene
        // while that is still loading.
        var imagePath = Paths.get("./schematics/test.png");
        byte[] imageContent = scene.exportAsPng(GAMESCENE_PLACEHOLDER_SCALE);
        if (imageContent != null) {
            Files.createDirectories(imagePath);
            Files.write(imagePath, imageContent);
        }
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
