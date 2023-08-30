package com.hollingsworth.schematic;

import com.hollingsworth.schematic.common.item.CafeItems;
import com.hollingsworth.schematic.common.network.Networking;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(SchematicMod.MODID)
public class SchematicMod {
    public static final String MODID = "cafetier";

    public SchematicMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        CafeItems.ITEMS.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        Networking.registerMessages();
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }
}
