package com.hollingsworth.schematic.api.blockprints.download;

public class PreviewDownloadResult {
    public final DownloadResponse downloadResponse;
    public final byte[] image;

    public PreviewDownloadResult(DownloadResponse downloadResponse, byte[] image) {
        this.downloadResponse = downloadResponse;
        this.image = image;
    }
}
