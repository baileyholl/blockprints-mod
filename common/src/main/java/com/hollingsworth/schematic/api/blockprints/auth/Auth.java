package com.hollingsworth.schematic.api.blockprints.auth;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hollingsworth.schematic.api.blockprints.ApiResponse;
import com.hollingsworth.schematic.api.blockprints.BlockprintsApi;
import com.hollingsworth.schematic.api.blockprints.RequestUtil;
import com.hollingsworth.schematic.oauth.Login;
import net.minecraft.client.Minecraft;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Auth {
    private final BlockprintsApi api;
    private final HttpClient CLIENT;

    public Auth(BlockprintsApi api) {
        this.api = api;
        this.CLIENT = this.api.CLIENT;
    }

    public ApiResponse<BlockprintsToken> postMSCode(String code) {
        try {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("code", code);
            jsonObject.addProperty("redirectUrl", Login.redirectUri);
            HttpRequest request = api.getBuilder()
                    .uri(RequestUtil.getRoute("/api/v1/auth/link/msauth"))
                    .POST(HttpRequest.BodyPublishers.ofString(jsonObject.toString())).build();
            var res = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            var statusCode = res.statusCode();
            if (!RequestUtil.responseSuccessful(statusCode)) {
                throw new Error("Failed to authenticate with Blockprints API");
            }
            JsonObject responseObj = JsonParser.parseString(res.body()).getAsJsonObject();
            return ApiResponse.success(new BlockprintsToken(responseObj.get("token").getAsString(), responseObj.get("expiresAt").getAsInt(), Minecraft.getInstance().player.getUUID()));
        }catch (Exception e){
            e.printStackTrace();
            return ApiResponse.unexpectedFailure();
        }
    }
}
