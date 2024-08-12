package com.hollingsworth.schematic;


import com.hollingsworth.schematic.platform.ForgePlatformHelper;
import com.hollingsworth.schematic.platform.Services;
import net.neoforged.fml.common.Mod;

@Mod(Constants.MOD_ID)
public class SchematicForge {
    
    public SchematicForge() {
        Services.PLATFORM = new ForgePlatformHelper();
        SchematicMod.init();
    }
}
