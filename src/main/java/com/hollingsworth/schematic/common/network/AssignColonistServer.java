package com.hollingsworth.schematic.common.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;

public class AssignColonistServer implements Message {
    public UUID colonistUUID;
    public BlockPos buildingPos;

    public AssignColonistServer(UUID colonistUUID, BlockPos buildingPos) {
        this.colonistUUID = colonistUUID;
        this.buildingPos = buildingPos;
    }

    public AssignColonistServer(FriendlyByteBuf buf) {
        this.decode(buf);
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeUUID(colonistUUID);
        buf.writeBlockPos(buildingPos);
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        colonistUUID = buf.readUUID();
        buildingPos = buf.readBlockPos();
    }

    @Override
    public void onServerReceived(MinecraftServer minecraftServer, ServerPlayer player, NetworkEvent.Context context) {

    }
}
