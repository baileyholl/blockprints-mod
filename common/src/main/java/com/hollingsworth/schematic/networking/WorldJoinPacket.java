package com.hollingsworth.schematic.networking;

import com.hollingsworth.schematic.ClientConstants;
import com.hollingsworth.schematic.SchematicMod;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

public class WorldJoinPacket  extends AbstractPacket{

    public static final Type<WorldJoinPacket> TYPE = new Type<>(SchematicMod.prefix("world_join"));
    public static final StreamCodec<RegistryFriendlyByteBuf, WorldJoinPacket> CODEC = StreamCodec.ofMember(WorldJoinPacket::toBytes, WorldJoinPacket::new);


    public WorldJoinPacket() {
    }

    public WorldJoinPacket(RegistryFriendlyByteBuf pb) {
    }

    public void toBytes(RegistryFriendlyByteBuf pb) {
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }


    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {
        ClientConstants.blockprintsServerside = true;
    }
}
