package com.hollingsworth.schematic.common.util;

import com.google.common.collect.Lists;
import com.hollingsworth.schematic.client.renderer.StatePos;
import com.hollingsworth.schematic.mixin.PaletteAccessor;
import com.hollingsworth.schematic.mixin.StructureTemplateAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BPStructureTemplate extends StructureTemplate {

    public BPStructureTemplate(ArrayList<StatePos> statePos){
        List<StructureBlockInfo> list = Lists.newArrayList();
        List<StructureBlockInfo> list1 = Lists.newArrayList();
        List<StructureBlockInfo> list2 = Lists.newArrayList();

        for(StatePos statePos1 : statePos){
            StructureBlockInfo structuretemplate$structureblockinfo = new StructureBlockInfo(statePos1.pos, statePos1.state, null);
            addToLists(structuretemplate$structureblockinfo, list, list1, list2);
        }
        var buildList = buildInfoList(list, list1, list2);
        StructureTemplateAccessor accessor = ((StructureTemplateAccessor) this);
        accessor.getPalettes().add(PaletteAccessor.createPalette(buildList));
    }
    private static void addToLists(StructureBlockInfo blockInfo, List<StructureBlockInfo> normalBlocks, List<StructureBlockInfo> blocksWithNbt, List<StructureBlockInfo> blocksWithSpecialShape) {
        if (blockInfo.nbt() != null) {
            blocksWithNbt.add(blockInfo);
        } else if (!blockInfo.state().getBlock().hasDynamicShape() && blockInfo.state().isCollisionShapeFullBlock(EmptyBlockGetter.INSTANCE, BlockPos.ZERO)) {
            normalBlocks.add(blockInfo);
        } else {
            blocksWithSpecialShape.add(blockInfo);
        }

    }

    private static List<StructureBlockInfo> buildInfoList(List<StructureBlockInfo> normalBlocks, List<StructureBlockInfo> blocksWithNbt, List<StructureBlockInfo> blocksWithSpecialShape) {
        Comparator<StructureBlockInfo> comparator = Comparator.comparingInt((structureBlockInfo) -> structureBlockInfo.pos().getY());
        Comparator<StructureBlockInfo> comparator2 = Comparator.comparingInt((structureBlockInfo) -> structureBlockInfo.pos().getX());
        Comparator<StructureBlockInfo> comparator3 = Comparator.comparingInt((structureBlockInfo) -> structureBlockInfo.pos().getZ());
        Comparator comparator1 = comparator.thenComparing(comparator2).thenComparing(comparator3);
        normalBlocks.sort(comparator1);
        blocksWithSpecialShape.sort(comparator1);
        blocksWithNbt.sort(comparator1);
        List<StructureBlockInfo> list = Lists.newArrayList();
        list.addAll(normalBlocks);
        list.addAll(blocksWithSpecialShape);
        list.addAll(blocksWithNbt);
        return list;
    }
}
