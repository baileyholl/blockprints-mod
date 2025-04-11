package com.hollingsworth.schematic.api.blockprints.auth;

import java.util.UUID;

public record BlockprintsToken (String token, int tokenExpiresSeconds, UUID requesterUUID){
    public boolean tokenExpired(){
        return tokenExpiresSeconds() < System.currentTimeMillis() / 1000;
    }
}
