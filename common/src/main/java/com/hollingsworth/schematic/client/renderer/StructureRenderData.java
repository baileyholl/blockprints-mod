package com.hollingsworth.schematic.client.renderer;

import com.hollingsworth.schematic.mixin.StructureTemplateAccessor;
import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StructureRenderData {
    public ArrayList<StatePos> statePosCache;
    public int sortCounter;
    public BoundingBox boundingBox;
    public BlockPos anchorPos;
    public Map<RenderType, BufferBuilder.SortState> sortStates = new HashMap<>();
    public String name;
    public String blockprintsId;
    public StructureRenderData(StructureTemplate structureTemplate, String name, String blockprintsId){
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
        this.name = name;
        this.blockprintsId = blockprintsId;
    }
}
