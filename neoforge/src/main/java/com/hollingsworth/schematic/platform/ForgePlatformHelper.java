package com.hollingsworth.schematic.platform;

import com.hollingsworth.schematic.Constants;
import com.hollingsworth.schematic.compat.CreateCompat;
import com.hollingsworth.schematic.platform.services.IPlatformHelper;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.common.util.BlockSnapshot;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.network.PacketDistributor;

import java.nio.file.Path;

public class ForgePlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "Neoforge";
    }

    @Override
    public Path getGameDirectory() {
        return FMLLoader.getGamePath();
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLLoader.isProduction();
    }

    @Override
    public Iterable<RenderType> getRenderTypes(BakedModel bakedModel, BlockState blockstate, RandomSource rand) {
        return bakedModel.getRenderTypes(blockstate, rand, ModelData.EMPTY);
    }

    @Override
    public void appendCreateGlue(Level level, AABB aabb, CompoundTag tag) {
        if(Constants.isCreateLoaded){
            CreateCompat.appendGlue(level, aabb, tag);
        }
    }

    @Override
    public boolean canPlaceBlock(ServerPlayer player, BlockState blockState, BlockPos pos) {
        return EventHooks.onBlockPlace(player, BlockSnapshot.create(player.level.dimension(), player.level, pos), Direction.UP);
    }

    @Override
    public void sendClientToServerPacket(CustomPacketPayload packet) {
        PacketDistributor.sendToServer(packet);
    }
}
