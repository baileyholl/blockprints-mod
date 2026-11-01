package com.hollingsworth.schematic.platform;

import com.hollingsworth.schematic.Constants;
import com.hollingsworth.schematic.compat.CreateCompat;
import com.hollingsworth.schematic.networking.AbstractPacket;
import com.hollingsworth.schematic.platform.services.IPlatformHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.nio.file.Path;

public class FabricPlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public Path getGameDirectory() {
        return FabricLoader.getInstance().getGameDir();
    }

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public void appendCreateGlue(Level level, AABB aabb, CompoundTag tag) {
        if(Constants.isCreateLoaded){
            CreateCompat.appendGlue(level, aabb, tag);
        }
    }

    @Override
    public void sendClientToServerPacket(AbstractPacket packet) {
        ClientPlayNetworking.send(packet);
    }

    @Override
    public void sendServerToClientPacket(AbstractPacket packet, ServerPlayer serverPlayer) {
        if(ServerPlayNetworking.canSend(serverPlayer, packet.type().id())) {
            ServerPlayNetworking.send(serverPlayer, packet);
        }
    }
}
