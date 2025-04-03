package com.hollingsworth.schematic.api.blockprints;

import com.hollingsworth.schematic.Constants;
import com.hollingsworth.schematic.api.blockprints.auth.Auth;
import com.hollingsworth.schematic.api.blockprints.auth.BlockprintsToken;
import com.hollingsworth.schematic.api.blockprints.download.Download;
import com.hollingsworth.schematic.api.blockprints.favorites.Favorites;
import com.hollingsworth.schematic.api.blockprints.upload.Upload;
import com.hollingsworth.schematic.oauth.TokenLoader;
import net.minecraft.client.Minecraft;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.time.Duration;

public class BlockprintsApi {

    private static BlockprintsApi INSTANCE;

    private final Download downloadApi;

    private final Favorites favoritesApi;

    private final Upload uploadApi;

    private final Auth authApi;

    public final HttpClient CLIENT;

    private BlockprintsToken bpToken;

    private static boolean loadedLocalToken = false;

    public BlockprintsApi() throws ApiError{
        this.CLIENT = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).connectTimeout(Duration.ofSeconds(30)).build();
        this.downloadApi = new Download(this);
        this.favoritesApi = new Favorites(this);
        this.uploadApi = new Upload(this);
        this.authApi = new Auth(this);
        if(!loadedLocalToken){
            this.bpToken = TokenLoader.loadToken();
            loadedLocalToken = true;
        }
    }

    public static BlockprintsApi getInstance() throws ApiError {
        var playerUuid = Minecraft.getInstance().player.getUUID();
        if (INSTANCE == null) {
            INSTANCE = new BlockprintsApi();
        }
        if(INSTANCE.tokenExpired() || (!playerUuid.equals(INSTANCE.bpToken.requesterUUID()) && !Constants.isDev)){
            INSTANCE.setToken(null);
        }
        return INSTANCE;
    }

    public void setToken(BlockprintsToken token){
        this.bpToken = token;
        if(token != null) {
            TokenLoader.writeToken(token);
        }
    }

    public HttpRequest.Builder getBuilder(boolean includeContentType){
        HttpRequest.Builder req = HttpRequest.newBuilder();
        if(bpToken != null){
            req = req.header("authorization", this.bpToken.token());
        }

        if(includeContentType){
            req.header("Content-Type", "application/json");
        }
        return req;
    }

    public HttpRequest.Builder getBuilder() {
        return getBuilder(true);
    }

    public boolean tokenExpired(){
        return this.bpToken == null || this.bpToken.tokenExpired();
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

    public Auth auth() {
        return authApi;
    }
}
