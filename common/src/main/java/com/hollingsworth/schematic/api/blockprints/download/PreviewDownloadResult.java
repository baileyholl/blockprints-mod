package com.hollingsworth.schematic.api.blockprints.download;

public class PreviewDownloadResult {
    public final GetSchematicResponse downloadResponse;
    public final byte[] image;

    public PreviewDownloadResult(GetSchematicResponse downloadResponse, byte[] image) {
        this.downloadResponse = downloadResponse;
        this.image = image;
    }
}
