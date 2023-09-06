package com.hollingsworth.schematic.common.network;

import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraftforge.network.NetworkEvent;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.zip.GZIPInputStream;

public class DownloadSchematic implements Message {
    public String colonistUUID;

    public DownloadSchematic(String colonistUUID) {
        this.colonistUUID = colonistUUID;
    }

    public DownloadSchematic(FriendlyByteBuf buf) {
        this.decode(buf);
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(colonistUUID);
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        colonistUUID = buf.readUtf();
    }

    @Override
    public void onServerReceived(MinecraftServer minecraftServer, ServerPlayer player, NetworkEvent.Context context) {
        try {
            URL website = new URL("https://storage.googleapis.com/schematic-mod/schematics/" + colonistUUID);
            ReadableByteChannel rbc = Channels.newChannel(website.openStream());
            FileOutputStream fos = new FileOutputStream("./schematics/test.nbt");
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            StructureTemplate t = new StructureTemplate();
            try (DataInputStream stream = new DataInputStream(new BufferedInputStream(
                    new GZIPInputStream(Files.newInputStream(Paths.get("./schematics/test.nbt"),StandardOpenOption.READ))))) {
                CompoundTag nbt = NbtIo.read(stream, new NbtAccounter(0x20000000L));
                t.load(minecraftServer.registries().compositeAccess().registryOrThrow(Registries.BLOCK).asLookup().filterFeatures(minecraftServer.getWorldData().enabledFeatures()),  nbt);
                t.placeInWorld(player.serverLevel(), player.getOnPos(), player.getOnPos(), new StructurePlaceSettings(), player.getRandom(), Block.UPDATE_CLIENTS);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
