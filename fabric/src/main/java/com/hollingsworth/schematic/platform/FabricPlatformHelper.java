package com.hollingsworth.schematic.platform;

import com.hollingsworth.schematic.Constants;
import com.hollingsworth.schematic.compat.CreateCompat;
import com.hollingsworth.schematic.platform.services.IPlatformHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.nio.file.Path;
import java.util.List;

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
    public Iterable<RenderType> getRenderTypes(BakedModel bakedModel, BlockState blockstate, RandomSource rand) {
        return List.of(ItemBlockRenderTypes.getChunkRenderType(blockstate));
    }

    @Override
    public void appendCreateGlue(Level level, AABB aabb, CompoundTag tag) {
        if(Constants.isCreateLoaded){
            CreateCompat.appendGlue(level, aabb, tag);
        }
    }
}
