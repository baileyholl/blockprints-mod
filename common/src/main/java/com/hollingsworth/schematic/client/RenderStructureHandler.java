package com.hollingsworth.schematic.client;

import com.hollingsworth.schematic.client.renderer.StructureRenderData;
import com.hollingsworth.schematic.client.renderer.StructureRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class RenderStructureHandler {
    public static boolean showRender = false;
    public static BlockPos anchorPos;

    public static StructureRenderData placingData;

    public static void startRender(StructureTemplate structureTemplate, String name, String bpId){
        showRender = true;
        anchorPos = null;
        placingData = new StructureRenderData(structureTemplate, name, bpId);
        StructureRenderer.structures.add(placingData);
    }

    public static void cancelRender(){
        showRender = false;
        anchorPos = null;
        StructureRenderer.structures.remove(placingData);
        placingData = null;
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
        placingData.anchorPos = RaycastHelper.getLookingAt(Minecraft.getInstance().player, true).getBlockPos();
    }

}
