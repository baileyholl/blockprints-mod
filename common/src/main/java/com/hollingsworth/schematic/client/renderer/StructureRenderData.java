package com.hollingsworth.schematic.client.renderer;

import com.hollingsworth.schematic.mixin.StructureTemplateAccessor;
import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
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
    public FakeRenderingWorld fakeRenderingWorld;
    public boolean updateRender;
    public StructureTemplate structureTemplate;
    public Rotation rotation;
    public Mirror mirror;

    public StructureRenderData(StructureTemplate structureTemplate, String name, String blockprintsId){
        var accessor = (StructureTemplateAccessor)structureTemplate;
        var palettes = accessor.getPalettes();
        if(palettes.isEmpty()){
            return;
        }
        var palette = palettes.get(0);
        statePosCache = new ArrayList<>();
        this.structureTemplate = structureTemplate;
        for(StructureTemplate.StructureBlockInfo blockInfo : palette.blocks()){
            statePosCache.add(new StatePos(blockInfo.state(), blockInfo.pos()));
        }
        boundingBox = structureTemplate.getBoundingBox(new StructurePlaceSettings(), new BlockPos(0, 0, 0));
        this.name = name;
        this.blockprintsId = blockprintsId;
        updateRender = true;
        rotation = Rotation.NONE;
        mirror = Mirror.NONE;
    }

    public void rotate(Rotation rotateBy){
        rotation = rotation.getRotated(rotateBy);
        statePosCache = StatePos.rotate(statePosCache, new ArrayList<>(), rotateBy);
        boundingBox = structureTemplate.getBoundingBox(new StructurePlaceSettings().setRotation(rotation), new BlockPos(0, 0, 0));
    }

    public void mirror(boolean mirror){
        this.mirror = mirror ? Mirror.FRONT_BACK : Mirror.NONE;

        boundingBox = structureTemplate.getBoundingBox(new StructurePlaceSettings().setMirror(this.mirror).setRotation(rotation), new BlockPos(0, 0, 0));
    }

}
