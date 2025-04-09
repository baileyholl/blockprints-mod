package com.hollingsworth.schematic.mixin;

import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(StructureTemplate.Palette.class)
public interface PaletteAccessor {
    @Invoker("<init>")
    static StructureTemplate.Palette createPalette(List<StructureTemplate.StructureBlockInfo> pBlocks) {
        throw new UnsupportedOperationException();
    }
}
