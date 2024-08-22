package com.hollingsworth.schematic.network;

import com.hollingsworth.schematic.SchematicMod;
import com.hollingsworth.schematic.client.renderer.StatePos;
import com.hollingsworth.schematic.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;

public record PlaceStructurePacket(List<StatePos> list) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<PlaceStructurePacket> TYPE = new CustomPacketPayload.Type<>(SchematicMod.prefix("storage_packet"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PlaceStructurePacket> CODEC = StreamCodec.ofMember(PlaceStructurePacket::toBytes, PlaceStructurePacket::fromBytes);

    public static PlaceStructurePacket fromBytes(RegistryFriendlyByteBuf registryFriendlyByteBuf) {
        List<StatePos> list = new ArrayList<>();
        int size = registryFriendlyByteBuf.readInt();
        for (int i = 0; i < size; i++) {
            BlockPos pos = registryFriendlyByteBuf.readBlockPos();
            CompoundTag tag = ByteBufCodecs.COMPOUND_TAG.decode(registryFriendlyByteBuf);
            list.add(new StatePos(NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), tag), pos));
        }
        return new PlaceStructurePacket(list);
    }


    public void toBytes(RegistryFriendlyByteBuf pb) {
        pb.writeInt(list.size());
        for (StatePos statePos : list) {
            pb.writeBlockPos(statePos.pos);
            ByteBufCodecs.COMPOUND_TAG.encode(pb, NbtUtils.writeBlockState(statePos.state));
        }
    }

    public static void handle(PlaceStructurePacket packet, ServerPlayer player) {
        if(!player.isCreative()){
            return;
        }
        for(StatePos statePos : packet.list){
            if(Services.PLATFORM.canPlaceBlock(player, statePos.state, statePos.pos)){
                player.level.setBlockAndUpdate(statePos.pos, statePos.state);
            }
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
