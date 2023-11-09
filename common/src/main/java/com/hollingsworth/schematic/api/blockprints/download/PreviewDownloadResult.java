package com.hollingsworth.schematic.api.blockprints.download;

import java.nio.file.Path;

public class PreviewDownloadResult {
    public final DownloadResponse downloadResponse;
    public final Path imagePath;

    public PreviewDownloadResult(DownloadResponse downloadResponse, Path imagePath) {
        this.downloadResponse = downloadResponse;
        this.imagePath = imagePath;
    }
}
