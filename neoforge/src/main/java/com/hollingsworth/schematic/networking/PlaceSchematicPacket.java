package com.hollingsworth.schematic.networking;

import com.hollingsworth.schematic.SchematicMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class PlaceSchematicPacket extends AbstractPacket{

    public static final Type<PlaceSchematicPacket> TYPE = new Type<>(SchematicMod.prefix("storage_packet"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PlaceSchematicPacket> CODEC = StreamCodec.ofMember(PlaceSchematicPacket::toBytes, PlaceSchematicPacket::new);

    public PlaceSchematicPacket(RegistryFriendlyByteBuf pb) {

    }

    public void toBytes(RegistryFriendlyByteBuf pb) {

    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public ClientboundCustomPayloadPacket toVanillaClientbound() {
        return super.toVanillaClientbound();
    }

    @Override
    public ServerboundCustomPayloadPacket toVanillaServerbound() {
        return super.toVanillaServerbound();
    }
}
