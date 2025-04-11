package com.hollingsworth.schematic.api.blockprints;

import com.hollingsworth.schematic.Constants;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class RequestUtil {

    public static String getDomain() {
        return Constants.isDev ? "http://127.0.0.1:3000" : "https://api.blockprints.io";
    }

    public static boolean responseSuccessful(int code) {
        return code / 100 == 2;
    }

    public static URI getRoute(String route) {
        return URI.create(getDomain() + route);
    }

    public static ApiResponse<Boolean> makeRequest(HttpRequest request, HttpClient client){
        try {
            var res = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (!RequestUtil.responseSuccessful(res.statusCode())) {
                return ApiResponse.parseServerError(res);
            }
            return ApiResponse.success();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return ApiResponse.connectionError();
        }
    }
}
