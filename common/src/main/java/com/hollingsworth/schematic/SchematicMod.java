package com.hollingsworth.schematic;

import com.hollingsworth.schematic.platform.Services;

public class SchematicMod {
    // This method serves as an initialization hook for the mod.
    public static void init() {
        Constants.LOG.info("Hello from Common init on {}! we are currently in a {} environment!", Services.PLATFORM.getPlatformName(), Services.PLATFORM.isDevelopmentEnvironment() ? "development" : "production");
        Constants.isDev = Services.PLATFORM.isDevelopmentEnvironment();
    }
}