package com.hollingsworth.schematic.mixin;

import net.minecraft.core.Vec3i;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(StructureTemplate.class)
public interface StructureTemplateAccessor {
    @Accessor
    List<StructureTemplate.Palette> getPalettes();
    @Accessor
    List<StructureTemplate.StructureEntityInfo> getEntityInfoList();
    @Accessor
    Vec3i getSize();
}
