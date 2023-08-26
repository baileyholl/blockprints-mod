package com.hollingsworth.schematic.common.network;

import com.hollingsworth.schematic.client.gui.CreateCafeScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;

public class OpenCreateScreen extends BaseMessage {
    public BlockPos deskPos;

    public OpenCreateScreen(BlockPos deskPos) {
        this.deskPos = deskPos;
    }

    public OpenCreateScreen(FriendlyByteBuf buf) {
        this.decode(buf);
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeLong(deskPos.asLong());
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        deskPos = BlockPos.of(buf.readLong());
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onClientReceived(Minecraft minecraft, Player player, NetworkEvent.Context context) {
        minecraft.setScreen(new CreateCafeScreen(deskPos));
    }

    @Override
    public void onServerReceived(MinecraftServer minecraftServer, ServerPlayer player, NetworkEvent.Context context) {
        Networking.sendToPlayerClient(new OpenCreateScreen(deskPos), player);
    }
}
