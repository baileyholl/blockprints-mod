package com.hollingsworth.schematic.client;

import com.hollingsworth.schematic.Constants;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

@EventBusSubscriber(value = Dist.CLIENT, modid = Constants.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ModBusClientEvent {

    @SubscribeEvent
    public static void registerKeyBindings(RegisterKeyMappingsEvent event) {
        for(ClientData.KeyFunction keyMapping : ClientData.KEY_FUNCTIONS){
            event.register(keyMapping.mapping());
        }
    }
}

