package com.hollingsworth.schematic;

import com.hollingsworth.schematic.client.ClientData;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.Minecraft;

public class SchematicFabricClient implements ClientModInitializer {
    private static final boolean[] WAS_DOWN = new boolean[ClientData.KEY_FUNCTIONS.length];
    private static boolean screenClosedThisTick;

    @Override
    public void onInitializeClient() {
        for(ClientData.KeyFunction keyMapping : ClientData.KEY_FUNCTIONS){
            KeyBindingHelper.registerKeyBinding(keyMapping.mapping());
        }
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> ScreenEvents.remove(screen).register(removed -> screenClosedThisTick = true));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            for(int i = 0; i < ClientData.KEY_FUNCTIONS.length; i++){
                ClientData.KeyFunction keyMapping = ClientData.KEY_FUNCTIONS[i];
                boolean down = keyMapping.mapping().isDown();
                if(down && !WAS_DOWN[i] && !screenClosedThisTick && client.screen == null){
                    keyMapping.function().run();
                }
                WAS_DOWN[i] = down;
            }
            screenClosedThisTick = false;
            if(Minecraft.getInstance().options.keyUse.isDown()){
                ClientData.rightClickEvent();
            }
        });

        WorldRenderEvents.AFTER_TRANSLUCENT.register((context) -> {
            ClientData.renderAfterTransparentBlocks(context.matrixStack(), context.projectionMatrix());
            ClientData.renderAfterSky(context.matrixStack());
        });

        HudRenderCallback.EVENT.register((matrixStack, tickDelta) -> {
            ClientData.renderGUIOverlayEvent(matrixStack, Minecraft.getInstance().getWindow());
        });
    }
}
