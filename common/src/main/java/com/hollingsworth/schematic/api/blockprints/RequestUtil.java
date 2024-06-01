package com.hollingsworth.schematic.api.blockprints;

import com.hollingsworth.schematic.Constants;

import java.net.URI;

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
}
