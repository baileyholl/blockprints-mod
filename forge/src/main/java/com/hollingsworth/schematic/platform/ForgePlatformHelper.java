package com.hollingsworth.schematic.platform;

import com.hollingsworth.schematic.platform.services.IPlatformHelper;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;

import java.nio.file.Path;

public class ForgePlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "Forge";
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
}
