package com.hollingsworth.schematic.common.util;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public class ClientUtil {
    public static void sendMessage(Component component){
        Player player = Minecraft.getInstance().player;
        if(player != null){
            player.sendSystemMessage(component);
        }
    }

    public static void sendMessage(String message){
        sendMessage(Component.translatable(message));
    }
}
