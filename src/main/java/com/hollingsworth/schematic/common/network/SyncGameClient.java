package com.hollingsworth.schematic.common.network;

import com.hollingsworth.schematic.client.CafeClientData;
import com.hollingsworth.schematic.client.ClientInfo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncGameClient {

    public CafeClientData cafeClientData;

    public SyncGameClient(CafeClientData cafeClientData){
        this.cafeClientData = cafeClientData;
    }

    public SyncGameClient(FriendlyByteBuf pb) {
        cafeClientData = new CafeClientData(pb.readNbt());
    }

    public void toBytes(FriendlyByteBuf pb) {
        pb.writeNbt(cafeClientData.toTag());
    }

    @SuppressWarnings("Convert2Lambda")
    public static boolean onMessage(SyncGameClient message, Supplier<NetworkEvent.Context> ctx) {
        if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
            ctx.get().enqueueWork(() -> {
                ClientInfo.cafeClientData = message.cafeClientData;
                ClientInfo.ticksToShowHUD = 100;
            });
        }
        ctx.get().setPacketHandled(true);
        return true;
    }

}
