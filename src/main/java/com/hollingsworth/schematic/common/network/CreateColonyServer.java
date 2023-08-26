package com.hollingsworth.schematic.common.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

public class CreateColonyServer implements Message{
    public String cafeName;
    public String description;
    public BlockPos cafePos;

    public CreateColonyServer(BlockPos cafePos, String cafeName, String description) {
        this.cafeName = cafeName;
        this.cafePos = cafePos;
        this.description = description;
    }

    public CreateColonyServer(FriendlyByteBuf buf) {
        this.decode(buf);
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeLong(cafePos.asLong());
        buf.writeUtf(cafeName);
        buf.writeUtf(description);
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        cafePos = BlockPos.of(buf.readLong());
        cafeName = buf.readUtf();
        description = buf.readUtf();
    }

    @Override
    public void onServerReceived(MinecraftServer minecraftServer, ServerPlayer player, NetworkEvent.Context context) {
    }
}
