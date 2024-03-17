package com.hollingsworth.schematic.client.renderer;

import com.hollingsworth.schematic.mixin.StructureTemplateAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.ArrayList;

public class StructureRenderData {
    public ArrayList<StatePos> statePosCache;
    public int sortCounter;
    public BoundingBox boundingBox;
    public BlockPos anchorPos;

    public StructureRenderData(StructureTemplate structureTemplate){
        var accessor = (StructureTemplateAccessor)structureTemplate;
        var palettes = accessor.getPalettes();
        if(palettes.isEmpty()){
            return;
        }
        var palette = palettes.get(0);
        statePosCache = new ArrayList<>();
        for(StructureTemplate.StructureBlockInfo blockInfo : palette.blocks()){
            statePosCache.add(new StatePos(blockInfo.state(), blockInfo.pos()));
        }
        boundingBox = structureTemplate.getBoundingBox(new StructurePlaceSettings(), new BlockPos(0, 0, 0));

    }


}
