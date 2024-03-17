package com.hollingsworth.schematic.client;

import com.hollingsworth.schematic.client.renderer.StructureRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class RenderStructureHandler {
    public static boolean showRender = false;
    public static BlockPos anchorPos;

    public static void startRender(StructureTemplate structureTemplate){
        showRender = true;
        anchorPos = null;
        StructureRenderer.loadFromStructure(structureTemplate);
    }

    public static void cancelRender(){
        showRender = false;
        anchorPos = null;
    }

    public static void onConfirmHit() {
        if (!RenderStructureHandler.showRender) {
            return;
        }
    }

    public static void onCancelHit() {
        if (!RenderStructureHandler.showRender) {
            return;
        }
        cancelRender();
    }

    public static void positionClicked() {
        if (!RenderStructureHandler.showRender) {
            return;
        }
        anchorPos = RaycastHelper.getLookingAt(Minecraft.getInstance().player, true).getBlockPos();

    }

}
