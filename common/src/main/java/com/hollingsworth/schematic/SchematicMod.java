package com.hollingsworth.schematic;

import com.hollingsworth.schematic.platform.Services;

public class SchematicMod {
    // This method serves as an initialization hook for the mod.
    public static void init() {
        Constants.isDev = Services.PLATFORM.isDevelopmentEnvironment();
        Constants.isCreateLoaded = Services.PLATFORM.isModLoaded("create");
    }
}