package com.hollingsworth.schematic.api.blockprints;

import com.hollingsworth.schematic.Constants;
import net.minecraft.client.Minecraft;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;

public class RequestUtil {

    public static final HttpClient CLIENT = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build();

    public static HttpRequest.Builder getBuilder(boolean includeContentType){
        HttpRequest.Builder req = null;
        if (Minecraft.getInstance().getUser().getName().equals("Dev")) {
            req = HttpRequest.newBuilder();
        }else{
            req = HttpRequest.newBuilder()
                    .header("authorization", "Bearer " + Minecraft.getInstance().getUser().getAccessToken());
        }
        if(includeContentType){
            req.header("Content-Type", "application/json");
        }
        return req;
    }

    public static HttpRequest.Builder getBuilder() {
        return getBuilder(true);
    }

    public static String getDomain() {
        return Constants.isDev ? "http://localhost:3000" : "https://api.blockprints.io";
    }

    public static boolean responseSuccessful(int code) {
        return code / 100 == 2;
    }

    public static URI getRoute(String route) {
        return URI.create(getDomain() + route);
    }
}
