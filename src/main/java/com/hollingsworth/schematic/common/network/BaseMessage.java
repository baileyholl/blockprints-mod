package com.hollingsworth.schematic.common.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;

public class BaseMessage implements Message{
    @Override
    public void encode(FriendlyByteBuf buf) {

    }

    @Override
    public void decode(FriendlyByteBuf buf) {

    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onClientReceived(Minecraft minecraft, Player player, NetworkEvent.Context context) {
        Message.super.onClientReceived(minecraft, player, context);
    }

    @Override
    public void onServerReceived(MinecraftServer minecraftServer, ServerPlayer player, NetworkEvent.Context context) {
        Message.super.onServerReceived(minecraftServer, player, context);
    }
}
