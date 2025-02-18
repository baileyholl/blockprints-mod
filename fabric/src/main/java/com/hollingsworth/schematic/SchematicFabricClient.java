package com.hollingsworth.schematic;

import com.hollingsworth.schematic.client.ClientData;
import com.hollingsworth.schematic.client.KeyEvent;
import com.hollingsworth.schematic.platform.FabricPlatformHelper;
import com.hollingsworth.schematic.platform.Services;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.Minecraft;

public class SchematicFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        Services.PLATFORM = new FabricPlatformHelper();
        for(ClientData.KeyFunction keyMapping : ClientData.KEY_FUNCTIONS){
            KeyBindingHelper.registerKeyBinding(keyMapping.mapping());
        }
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            for(ClientData.KeyFunction keyMapping : ClientData.KEY_FUNCTIONS){
                keyMapping.function().accept(new KeyEvent(keyMapping.mapping(), keyMapping.mapping().isDown() ? 1 : 0));
            }
            if(Minecraft.getInstance().options.keyUse.isDown()){
                ClientData.rightClickEvent();
            }
            ClientData.tickEvent();
        });

        WorldRenderEvents.LAST.register((context) -> {
            ClientData.renderAfterTransparentBlocks(context.matrixStack(), context.projectionMatrix(), context.positionMatrix());
        });

        WorldRenderEvents.END.register((context) -> {
            ClientData.renderAfterSky(context.matrixStack(), context.positionMatrix());
        });

        HudRenderCallback.EVENT.register((matrixStack, tickDelta) -> {
            ClientData.renderGUIOverlayEvent(matrixStack, Minecraft.getInstance().getWindow());
        });
    }
}
