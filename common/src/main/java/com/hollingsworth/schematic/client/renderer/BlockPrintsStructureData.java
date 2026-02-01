package com.hollingsworth.schematic.client.renderer;

import com.hollingsworth.nuggets.client.area_capture.StructureRenderData;
import com.hollingsworth.nuggets.client.rendering.FakeRenderingWorld;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.VertexBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class BlockPrintsStructureData extends StructureRenderData {
    public Map<RenderType, MeshData.SortState> sortStates = new HashMap<>();
    public Map<RenderType, MeshData> meshDatas = new HashMap<>();
    public String name;
    public String blockprintsId;
    public FakeRenderingWorld fakeRenderingWorld;
    public int sortCounter;
    //A map of RenderType -> DireBufferBuilder, so we can draw the different render types in proper order later
    public final Map<RenderType, ByteBufferBuilder> builders = RenderType.chunkBufferLayers().stream().collect(Collectors.toMap((renderType) -> renderType, (type) -> new ByteBufferBuilder(type.bufferSize())));
    //A map of RenderType -> Vertex Buffer to buffer the different render types.
    public Map<RenderType, VertexBuffer> vertexBuffers = RenderType.chunkBufferLayers().stream().collect(Collectors.toMap((renderType) -> renderType, (type) -> new VertexBuffer(VertexBuffer.Usage.STATIC)));
    public final Map<RenderType, BufferBuilder> bufferBuilders = new HashMap<>();

    public BlockPrintsStructureData(StructureTemplate structureTemplate, String name, String blockprintsId){
        super(structureTemplate);
        this.name = name;
        this.blockprintsId = blockprintsId;
    }

    //Get the buffer from the map, and ensure its building
    public ByteBufferBuilder getByteBuffer(RenderType renderType) {
        return builders.get(renderType);
    }
}
