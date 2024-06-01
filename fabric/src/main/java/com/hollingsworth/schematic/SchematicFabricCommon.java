package com.hollingsworth.schematic;

import net.fabricmc.api.ModInitializer;

public class SchematicFabricCommon implements ModInitializer {
    
    @Override
    public void onInitialize() {
        SchematicMod.init();
    }
}
