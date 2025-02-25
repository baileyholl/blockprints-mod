package com.hollingsworth.schematic.common;

import com.hollingsworth.schematic.networking.WorldJoinPacket;
import com.hollingsworth.schematic.platform.Services;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public class ServerEvents {

    public static void onWorldJoin(PlayerEvent.PlayerLoggedInEvent event){
        if(event.getEntity() instanceof ServerPlayer serverPlayer){
            Services.PLATFORM.sendServerToClientPacket(new WorldJoinPacket(), serverPlayer);
        }
    }

}
