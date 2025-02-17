package com.hollingsworth.schematic.client.renderer;

import com.hollingsworth.schematic.mixin.StructureTemplateAccessor;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.VertexBuffer;
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
import java.util.stream.Collectors;

public class StructureRenderData {
    public ArrayList<StatePos> statePosCache;
    public BoundingBox boundingBox;
    public BlockPos anchorPos;
    public Map<RenderType, MeshData.SortState> sortStates = new HashMap<>();
    public Map<RenderType, MeshData> meshDatas = new HashMap<>();
    public String name;
    public String blockprintsId;
    public FakeRenderingWorld fakeRenderingWorld;
    public StructureTemplate structureTemplate;
    public Rotation rotation;
    public Mirror mirror;
    public boolean flipped = false;
    public BlockPos lastRenderPos = null;
    public int sortCounter;
    //A map of RenderType -> DireBufferBuilder, so we can draw the different render types in proper order later
    public final Map<RenderType, ByteBufferBuilder> builders = RenderType.chunkBufferLayers().stream().collect(Collectors.toMap((renderType) -> renderType, (type) -> new ByteBufferBuilder(type.bufferSize())));
    //A map of RenderType -> Vertex Buffer to buffer the different render types.
    public Map<RenderType, VertexBuffer> vertexBuffers = RenderType.chunkBufferLayers().stream().collect(Collectors.toMap((renderType) -> renderType, (type) -> new VertexBuffer(VertexBuffer.Usage.STATIC)));
    public final Map<RenderType, BufferBuilder> bufferBuilders = new HashMap<>();
    public StructurePlaceSettings structurePlaceSettings;

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
        structurePlaceSettings = new StructurePlaceSettings();
        boundingBox = structureTemplate.getBoundingBox(structurePlaceSettings, new BlockPos(0, 0, 0));
        this.name = name;
        this.blockprintsId = blockprintsId;
        rotation = Rotation.NONE;
        mirror = Mirror.NONE;
    }

    public void rotate(Rotation rotateBy){
        rotation = rotation.getRotated(rotateBy);
        statePosCache = StatePos.rotate(statePosCache, new ArrayList<>(), rotateBy);
        boundingBox = structureTemplate.getBoundingBox(structurePlaceSettings.setRotation(rotation), new BlockPos(0, 0, 0));
    }

    public void mirror(boolean mirror){
        this.mirror = mirror ? Mirror.FRONT_BACK : Mirror.NONE;

        boundingBox = structureTemplate.getBoundingBox(structurePlaceSettings.setMirror(this.mirror), new BlockPos(0, 0, 0));
    }

    public void flip(){
        flipped = !flipped;
        this.mirror(flipped);
    }

    //Get the buffer from the map, and ensure its building
    public ByteBufferBuilder getByteBuffer(RenderType renderType) {
        return builders.get(renderType);
    }
}
