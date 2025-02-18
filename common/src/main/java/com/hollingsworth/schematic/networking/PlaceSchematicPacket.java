package com.hollingsworth.schematic.networking;

import com.hollingsworth.schematic.SchematicMod;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class PlaceSchematicPacket extends AbstractPacket{

    public static final Type<PlaceSchematicPacket> TYPE = new Type<>(SchematicMod.prefix("storage_packet"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PlaceSchematicPacket> CODEC = StreamCodec.ofMember(PlaceSchematicPacket::toBytes, PlaceSchematicPacket::new);

    StructureTemplate template;
    StructurePlaceSettings structurePlaceSettings;
    CompoundTag templateTag;
    BlockPos pos;

    public PlaceSchematicPacket(StructureTemplate template, StructurePlaceSettings structurePlaceSettings, BlockPos pos) {
        this.template = template;
        this.structurePlaceSettings = structurePlaceSettings;
        this.pos = pos;
    }

    public PlaceSchematicPacket(RegistryFriendlyByteBuf pb) {
        templateTag = ByteBufCodecs.COMPOUND_TAG.decode(pb);
        pos = pb.readBlockPos();
        structurePlaceSettings = new StructurePlaceSettings();
        structurePlaceSettings.setMirror(pb.readEnum(Mirror.class));
        structurePlaceSettings.setRotation(pb.readEnum(Rotation.class));
    }

    public void toBytes(RegistryFriendlyByteBuf pb) {
        ByteBufCodecs.COMPOUND_TAG.encode(pb, template.save(new CompoundTag()));
        pb.writeBlockPos(pos);
        pb.writeEnum(structurePlaceSettings.getMirror());
        pb.writeEnum(structurePlaceSettings.getRotation());

    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void onServerReceived(MinecraftServer minecraftServer, ServerPlayer player) {
        template = new StructureTemplate();
        template.load(player.level.registryAccess().lookupOrThrow(BuiltInRegistries.BLOCK.key()), templateTag);
        template.placeInWorld(player.serverLevel(), pos, pos, structurePlaceSettings,  RandomSource.create(Util.getMillis()), 3);
    }
}
