package com.hollingsworth.schematic.api.blockprints;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.network.chat.Component;

import java.net.http.HttpResponse;

public class ApiResponse<T> {
    public final T response;
    public final String error;

    private ApiResponse(T response) {
        this.response = response;
        this.error = null;
    }

    private ApiResponse(String error) {
        this.response = null;
        this.error = error;
    }

    private ApiResponse(T response, String error) {
        this.response = response;
        this.error = error;
    }

    public boolean wasSuccessful() {
        return error == null;
    }

    public ApiResponse<Boolean> toBoolean(){
        return new ApiResponse<>(response != null, error);
    }

    public static <T> ApiResponse<T> error(Component component) {
        return new ApiResponse<>(component.getString());
    }

    public static <T> ApiResponse<T> error(String error){
        return new ApiResponse<>(error);
    }

    public static <T> ApiResponse<T> error(ApiResponse<?> response){
        return new ApiResponse<>(response.error);
    }

    public static ApiResponse<Boolean> success() {
        return new ApiResponse<>(true);
    }

    public static <T> ApiResponse<T> success(T response) {
        return new ApiResponse<>(response);
    }

    public static <F> ApiResponse<F> connectionError() {
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
            return ApiResponse.error(component);
        } catch (Exception e) {
            return unexpectedFailure();
        }
    }
}
