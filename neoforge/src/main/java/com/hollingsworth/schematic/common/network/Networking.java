package com.hollingsworth.schematic.common.network;

import com.hollingsworth.schematic.network.PlaceStructurePacket;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class Networking {

    public static void register(final RegisterPayloadHandlersEvent event) {
        // Sets the current network version
        final PayloadRegistrar reg = event.registrar("1");
        reg.playToServer(PlaceStructurePacket.TYPE, PlaceStructurePacket.CODEC, ( packet, ctx) -> {
            PlaceStructurePacket.handle(packet, (ServerPlayer) ctx.player());
        });
    }
}
