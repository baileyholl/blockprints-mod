package com.hollingsworth.schematic.client.renderer;

import com.hollingsworth.nuggets.client.rendering.CachedStructureRenderData;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class BlockPrintsStructureData extends CachedStructureRenderData {
    public String name;
    public String blockprintsId;

    public BlockPrintsStructureData(StructureTemplate structureTemplate, String name, String blockprintsId){
        super(structureTemplate);
        this.name = name;
        this.blockprintsId = blockprintsId;
    }
}
