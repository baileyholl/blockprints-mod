package com.hollingsworth.schematic;


import com.hollingsworth.schematic.common.ServerEvents;
import com.hollingsworth.schematic.networking.Networking;
import com.hollingsworth.schematic.platform.ForgePlatformHelper;
import com.hollingsworth.schematic.platform.Services;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

@Mod(Constants.MOD_ID)
public class SchematicForge {
    
    public SchematicForge(IEventBus modEventBus, ModContainer modContainer) {
        Services.PLATFORM = new ForgePlatformHelper();
        SchematicMod.init();
        modEventBus.addListener(Networking::register);
        NeoForge.EVENT_BUS.addListener(ServerEvents::onWorldJoin);
    }
}
