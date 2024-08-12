package com.hollingsworth.schematic;

import com.hollingsworth.schematic.platform.FabricPlatformHelper;
import com.hollingsworth.schematic.platform.Services;
import net.fabricmc.api.ModInitializer;

public class SchematicFabricCommon implements ModInitializer {
    
    @Override
    public void onInitialize() {
        Services.PLATFORM = new FabricPlatformHelper();
        SchematicMod.init();
    }
}
