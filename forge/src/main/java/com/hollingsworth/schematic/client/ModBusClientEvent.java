package com.hollingsworth.schematic.client;

import com.hollingsworth.schematic.Constants;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModBusClientEvent {

    @SubscribeEvent
    public static void registerKeyBindings(RegisterKeyMappingsEvent event) {
        for(ClientData.KeyFunction keyMapping : ClientData.KEY_FUNCTIONS){
            event.register(keyMapping.mapping());
        }
    }
}

