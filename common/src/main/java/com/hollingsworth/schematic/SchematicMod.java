package com.hollingsworth.schematic;

import com.hollingsworth.schematic.platform.Services;
import net.minecraft.resources.ResourceLocation;

public class SchematicMod {
    // This method serves as an initialization hook for the mod.
    public static void init() {
        Constants.isDev = Services.PLATFORM.isDevelopmentEnvironment();
        Constants.isCreateLoaded = Services.PLATFORM.isModLoaded("create");
    }

    public static ResourceLocation prefix(String path) {
        return ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, path);
    }
}