package com.hollingsworth.schematic.oauth;

import com.hollingsworth.schematic.api.blockprints.BlockprintsApi;
import com.hollingsworth.schematic.api.blockprints.auth.BlockprintsToken;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.bootstrap.HttpServer;
import org.apache.http.impl.bootstrap.ServerBootstrap;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class Login {

    public static final String clientId = "874a3069-29ab-48a1-98d1-1d0c912e7b98";
    public static HttpServer server;
    // Used later when we get a callback from the server
    public static String redirectUri = "";
    public static void startOAuthFlow(Consumer<BlockprintsToken> onSuccess) throws IOException {
        if (server != null) {
            server.stop();
        }
        server = null;
        for(int i = 0; i < 10; i++){
            try{
                server = bootOnPort(3000 + i, onSuccess);
                redirectUri = "http://localhost:" + (3000 + i) + "/ms";
                break;
            }catch (Exception e){
                server = null;
            }
        }
        if(server != null) {
            server.start();
        }else{
            throw new IOException("Failed to find open port");
        }
        Util.getPlatform().openUri("https://login.microsoftonline.com/consumers/oauth2/v2.0/authorize" +
                "?client_id=" + clientId +
                "&response_type=code" +
                "&redirect_uri=" + URLEncoder.encode(redirectUri, "UTF-8") +
                "&response_mode=query" +
                "&scope=" + URLEncoder.encode("XboxLive.signin offline_access", "UTF-8") +
                "&state=12345");
    }

    private static HttpServer bootOnPort(int port, Consumer<BlockprintsToken> onSuccess) throws IOException{
        server = ServerBootstrap.bootstrap()
                .setListenerPort(port)
                .registerHandler("/ms", (httpRequest, httpResponse, httpContext) -> {
                    httpResponse.setEntity(new StringEntity("<script> javascript:window.close()</script>", ContentType.TEXT_HTML));
                    httpResponse.setStatusCode(200);
                    try {
                        List<NameValuePair> pairs = new URIBuilder(httpRequest.getRequestLine().getUri()).getQueryParams();
                        String msCode = pairs.get(0).getValue();

                        CompletableFuture.supplyAsync(() -> BlockprintsApi.getInstance().auth().postMSCode(msCode), Minecraft.getInstance())
                                .thenAcceptAsync((response) -> {
                                    CompletableFuture.supplyAsync(() -> {
                                        if (response.wasSuccessful()) {
                                            onSuccess.accept(response.response);
                                        }
                                        return response;
                                    }, Minecraft.getInstance());
                                }, Minecraft.getInstance()).get();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    server.stop();
                })
                .create();
        server.start();
        return server;
    }

    public static void abortAuth() {
        if (server != null) {
            server.stop();
        }
    }
}
