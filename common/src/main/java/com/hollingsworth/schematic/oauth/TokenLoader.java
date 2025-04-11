package com.hollingsworth.schematic.oauth;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hollingsworth.schematic.api.blockprints.auth.BlockprintsToken;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class TokenLoader {
    public static void writeToken(BlockprintsToken token){
        try {
            if(token == null || token.tokenExpired()){
                return;
            }
            Path path = Paths.get(System.getProperty("user.home"), "blockprints", "data.json");
            Files.createDirectories(path.getParent());
            JsonObject element = new JsonObject();
            element.addProperty("token", token.token());
            element.addProperty("uuid", token.requesterUUID().toString());
            element.addProperty("expiresAt", token.tokenExpiresSeconds());
            if(!Files.exists(path)){
                Files.createFile(path);
            }
            Files.writeString(path, element.toString(), StandardCharsets.UTF_8);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static @Nullable BlockprintsToken loadToken(){
        try {
            Path path = Paths.get(System.getProperty("user.home"), "blockprints", "data.json");
            if (Files.exists(path)) {
                String content = Files.readString(path, StandardCharsets.UTF_8);
                JsonObject element = JsonParser.parseString(content).getAsJsonObject();
                BlockprintsToken token = new BlockprintsToken(element.get("token").getAsString(), element.get("expiresAt").getAsInt(), UUID.fromString(element.get("uuid").getAsString()));
                return token;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
