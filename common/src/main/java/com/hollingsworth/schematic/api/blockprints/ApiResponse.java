package com.hollingsworth.schematic.api.blockprints;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.network.chat.Component;

import java.net.http.HttpResponse;

public class ApiResponse<T> {
    public final T response;
    public final String error;

    public ApiResponse(T response) {
        this.response = response;
        this.error = null;
    }

    public ApiResponse(String error) {
        this.response = null;
        this.error = error;
    }

    public ApiResponse(Component component) {
        this.response = null;
        this.error = component.getString();
    }

    public static <F> ApiResponse<F> expectedFailure() {
        return new ApiResponse<>(Component.translatable("blockprints.error_loading").getString());
    }

    public static <F> ApiResponse<F> unexpectedFailure() {
        return new ApiResponse<>(Component.translatable("blockprints.unexpected_error").getString());
    }

    public static <F> ApiResponse<F> parseServerError(HttpResponse<String> error) {
        try {
            JsonObject responseObj = JsonParser.parseString(error.body()).getAsJsonObject();
            String errorString = responseObj.get("error").getAsString();
            Component component = Component.translatable("blockprints.error_received", errorString);
            return new ApiResponse<>(component);
        } catch (Exception e) {
            return unexpectedFailure();
        }
    }
}
