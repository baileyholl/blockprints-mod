package com.hollingsworth.schematic;

import com.hollingsworth.schematic.networking.PlaceSchematicPacket;
import com.hollingsworth.schematic.platform.FabricPlatformHelper;
import com.hollingsworth.schematic.platform.Services;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class SchematicFabricCommon implements ModInitializer {
    
    @Override
    public void onInitialize() {
        Services.PLATFORM = new FabricPlatformHelper();
        SchematicMod.init();
        PayloadTypeRegistry.playC2S().register(PlaceSchematicPacket.TYPE, PlaceSchematicPacket.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(PlaceSchematicPacket.TYPE, (payload, ctx) -> {
            payload.onServerReceived(ctx.server(), ctx.player());
        });
    }
}
