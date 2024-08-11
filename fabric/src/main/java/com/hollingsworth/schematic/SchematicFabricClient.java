package com.hollingsworth.schematic;

import com.hollingsworth.schematic.client.ClientData;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.Minecraft;

public class SchematicFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        for(ClientData.KeyFunction keyMapping : ClientData.KEY_FUNCTIONS){
            KeyBindingHelper.registerKeyBinding(keyMapping.mapping());
        }
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            for(ClientData.KeyFunction keyMapping : ClientData.KEY_FUNCTIONS){
                if(keyMapping.mapping().isDown()){
                    keyMapping.function().run();
                }
            }
            if(Minecraft.getInstance().options.keyUse.isDown()){
                ClientData.rightClickEvent();
            }
        });

        WorldRenderEvents.LAST.register((context) -> {
            ClientData.renderAfterTransparentBlocks(context.matrixStack(), context.projectionMatrix(), context.positionMatrix());
            ClientData.renderAfterSky(context.matrixStack(), context.positionMatrix());
        });

        HudRenderCallback.EVENT.register((matrixStack, tickDelta) -> {
            ClientData.renderGUIOverlayEvent(matrixStack, Minecraft.getInstance().getWindow());
        });
    }
}
