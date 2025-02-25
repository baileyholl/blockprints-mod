package com.hollingsworth.schematic;

import com.hollingsworth.schematic.networking.PlaceSchematicPacket;
import com.hollingsworth.schematic.networking.WorldJoinPacket;
import com.hollingsworth.schematic.platform.FabricPlatformHelper;
import com.hollingsworth.schematic.platform.Services;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class SchematicFabricCommon implements ModInitializer {
    
    @Override
    public void onInitialize() {
        Services.PLATFORM = new FabricPlatformHelper();
        SchematicMod.init();
        PayloadTypeRegistry.playC2S().register(PlaceSchematicPacket.TYPE, PlaceSchematicPacket.CODEC);
        PayloadTypeRegistry.playS2C().register(WorldJoinPacket.TYPE, WorldJoinPacket.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(PlaceSchematicPacket.TYPE, (payload, ctx) -> {
            payload.onServerReceived(ctx.server(), ctx.player());
        });

        ClientPlayNetworking.registerGlobalReceiver(WorldJoinPacket.TYPE, (payload, ctx) -> {
            payload.onClientReceived(ctx.client(), ctx.player());
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            Services.PLATFORM.sendServerToClientPacket(new WorldJoinPacket(), handler.player);
        });
    }
}
