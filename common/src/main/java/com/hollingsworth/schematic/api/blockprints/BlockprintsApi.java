package com.hollingsworth.schematic.api.blockprints;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hollingsworth.schematic.api.blockprints.download.Download;
import com.hollingsworth.schematic.api.blockprints.favorites.Favorites;
import com.hollingsworth.schematic.api.blockprints.upload.Upload;
import net.minecraft.client.Minecraft;
import org.apache.http.client.utils.URIBuilder;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.UUID;

public class BlockprintsApi {

    private static BlockprintsApi INSTANCE;

    private final Download downloadApi;

    private final Favorites favoritesApi;

    private final Upload uploadApi;

    public final HttpClient CLIENT;

    private String bpToken = null;
    // NumericDate of when the token expires (seconds)
    private int bpTokenExpires = 0;

    private UUID requesterUUID = null;

    public BlockprintsApi() throws ApiError{
        this.CLIENT = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).connectTimeout(Duration.ofSeconds(30)).build();
        try {
            URIBuilder uriBuilder = new URIBuilder(RequestUtil.getDomain() + "/api/v1/auth");
            HttpRequest.Builder reqBuilder = null;
            if (Minecraft.getInstance().getUser().getName().equals("Dev") || Minecraft.getInstance().getUser().getAccessToken().equals("FabricMC")) {
                reqBuilder = HttpRequest.newBuilder();
            }else{
                reqBuilder = HttpRequest.newBuilder()
                        .header("authorization", "Bearer " + Minecraft.getInstance().getUser().getAccessToken());
            }
            HttpRequest request = reqBuilder.header("Content-Type", "application/json")
                    .uri(uriBuilder.build())
                    .timeout(Duration.ofSeconds(8))
                    .GET().build();
            var res = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            var code = res.statusCode();
            if (!RequestUtil.responseSuccessful(code)) {
                throw new Error("Failed to authenticate with Blockprints API");
            }
            JsonObject responseObj = JsonParser.parseString(res.body()).getAsJsonObject();
            this.bpToken = responseObj.get("token").getAsString();
            this.bpTokenExpires = responseObj.get("expiresAt").getAsInt();
            this.requesterUUID = Minecraft.getInstance().player.getUUID();
        } catch (URISyntaxException | IOException | InterruptedException | Error e) {
            e.printStackTrace();
            throw new ApiError("Failed to authenticate with Blockprints API. Are you logged in to Minecraft?");
        }
        this.downloadApi = new Download(this);
        this.favoritesApi = new Favorites(this);
        this.uploadApi = new Upload(this);
    }

    public static BlockprintsApi getInstance() throws ApiError {
        var playerUuid = Minecraft.getInstance().player.getUUID();
        if (INSTANCE == null || INSTANCE.tokenExpired() || INSTANCE.requesterUUID == null || !playerUuid.equals(INSTANCE.requesterUUID)) {
            INSTANCE = new BlockprintsApi();
        }
        return INSTANCE;
    }

    public HttpRequest.Builder getBuilder(boolean includeContentType){
        HttpRequest.Builder req = HttpRequest.newBuilder().header("authorization", this.bpToken);

        if(includeContentType){
            req.header("Content-Type", "application/json");
        }
        return req;
    }

    public HttpRequest.Builder getBuilder() {
        return getBuilder(true);
    }

    public boolean tokenExpired(){
        return this.bpToken == null || this.bpTokenExpires < System.currentTimeMillis() / 1000;
    }

    public Download download() {
        return downloadApi;
    }

    public Favorites favorites() {
        return favoritesApi;
    }

    public Upload upload() {
        return uploadApi;
    }
}
