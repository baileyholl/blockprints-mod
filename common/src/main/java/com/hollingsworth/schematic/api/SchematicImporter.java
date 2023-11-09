package com.hollingsworth.schematic.api;

import com.hollingsworth.schematic.api.blockprints.GoogleCloudStorage;
import com.hollingsworth.schematic.api.blockprints.download.Download;
import com.hollingsworth.schematic.api.blockprints.download.DownloadResponse;
import com.hollingsworth.schematic.client.ClientData;
import com.hollingsworth.schematic.common.util.ClientUtil;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public class SchematicImporter {

    public static CompletableFuture<PreviewDownloadResult> downloadPreview(String id) {
        return CompletableFuture.<PreviewDownloadResult>supplyAsync(() -> {
            try {
                ClientData.setStatus(Component.translatable("blockprints.downloading_preview"));
                var result = Download.getSchematic(id);
                if (result == null) {
                    ClientUtil.sendMessage(Component.translatable("blockprints.download_not_found"));
                    return null;
                }
                var downloaded = GoogleCloudStorage.downloadImage(result.previewImage, result.structureName);
                if (downloaded == null) {
                    ClientUtil.sendMessage(Component.translatable("blockprints.download_failed"));
                    return null;
                }
                return new PreviewDownloadResult(result, downloaded);
            } catch (Exception e) {
                e.printStackTrace();
                ClientUtil.sendMessage(Component.translatable("blockprints.download_failed"));
            }
            return null;
        }, Util.backgroundExecutor());
    }

    public static CompletableFuture<Boolean> downloadSchematic(String link, String name) {
        return CompletableFuture.<Boolean>supplyAsync(() -> {
            try {
                ClientData.setStatus(Component.translatable("blockprints.downloading_schematic"));
                var downloaded = GoogleCloudStorage.downloadSchematic(link, name);
                if (downloaded == null) {
                    ClientUtil.sendMessage(Component.translatable("blockprints.download_failed"));
                    return false;
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }, Util.backgroundExecutor());
    }

    public static class PreviewDownloadResult {
        public final DownloadResponse downloadResponse;
        public final Path imagePath;

        public PreviewDownloadResult(DownloadResponse downloadResponse, Path imagePath) {
            this.downloadResponse = downloadResponse;
            this.imagePath = imagePath;
        }
    }

}
