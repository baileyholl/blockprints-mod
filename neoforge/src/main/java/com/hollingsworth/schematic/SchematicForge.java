package com.hollingsworth.schematic;


import com.hollingsworth.schematic.common.network.Networking;
import com.hollingsworth.schematic.platform.ForgePlatformHelper;
import com.hollingsworth.schematic.platform.Services;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(Constants.MOD_ID)
public class SchematicForge {
    
    public SchematicForge(IEventBus modEventBus, ModContainer modContainer) {
        Services.PLATFORM = new ForgePlatformHelper();
        SchematicMod.init();
        modEventBus.addListener(Networking::register);
    }
}
