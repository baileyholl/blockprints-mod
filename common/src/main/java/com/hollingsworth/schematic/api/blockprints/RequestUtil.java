package com.hollingsworth.schematic.api.blockprints;

import com.hollingsworth.schematic.Constants;
import net.minecraft.client.Minecraft;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;

public class RequestUtil {

    public static final HttpClient CLIENT = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build();

    public static HttpRequest.Builder getBuilder() {
        if (Minecraft.getInstance().getUser().getName().equals("Dev")) {
            return HttpRequest.newBuilder()
                    .header("Content-Type", "application/json");
        }
        return HttpRequest.newBuilder()
                .header("Content-Type", "application/json")
                .header("authorization", "Bearer " + Minecraft.getInstance().getUser().getAccessToken());
    }

    public static String getDomain() {
        //TODO: Change this to the actual hosted domain
        return Constants.isDev || true ? "http://localhost:3000" : "";
    }

    public static boolean responseSuccessful(int code) {
        return code / 100 == 2;
    }

    public static URI getRoute(String route) {
        return URI.create(getDomain() + route);
    }
}
