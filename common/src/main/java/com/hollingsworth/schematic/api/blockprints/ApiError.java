package com.hollingsworth.schematic.api.blockprints;

import net.minecraft.network.chat.Component;

public class ApiError extends Error{

    public ApiError(String message) {
        super(message);
    }

    public <T> ApiResponse<T> toApiResponse() {
        return ApiResponse.error(Component.literal(this.getMessage()));
    }
}
