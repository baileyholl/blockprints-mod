package com.hollingsworth.schematic.client;

import com.hollingsworth.schematic.Constants;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Constants.MOD_ID)
public class ClientInfo {
    public static int ticksInGame = 0;
    public static ClientData cafeClientData = null;
    public static int ticksToShowHUD = 0;
    @SubscribeEvent
    public static void clientTickEnd(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            ClientInfo.ticksInGame++;
            if(ticksToShowHUD > 0){
                ticksToShowHUD--;
            }
        }
    }
}
