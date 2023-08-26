package com.hollingsworth.schematic.common.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

public class StartGameServer implements Message{

    public BlockPos cafePos;

    public StartGameServer(BlockPos cafePos) {
        this.cafePos = cafePos;
    }

    public StartGameServer(FriendlyByteBuf buf) {
        decode(buf);
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeLong(cafePos.asLong());
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        cafePos = BlockPos.of(buf.readLong());
    }

    @Override
    public void onServerReceived(MinecraftServer minecraftServer, ServerPlayer player, NetworkEvent.Context context) {

    }
}
