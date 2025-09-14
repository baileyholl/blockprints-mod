package com.hollingsworth.schematic.common.lib;

import com.google.common.collect.Lists;
import com.hollingsworth.schematic.mixin.StructureTemplateAccessor;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Clearable;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.BitSetDiscreteVoxelShape;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;

import java.util.Iterator;
import java.util.List;

public class BPStructureTemplate extends StructureTemplate {
    public boolean placeInWorld(ServerLevelAccessor pServerLevel, BlockPos pOffset, BlockPos pPos, StructurePlaceSettings pSettings, RandomSource pRandom, int pFlags) {
        StructureTemplateAccessor accessor = (StructureTemplateAccessor) this;
        List<Palette> palettes = accessor.getPalettes();
        List<StructureTemplate.StructureEntityInfo> entityInfoList = accessor.getEntityInfoList();
        Vec3i size = accessor.getSize();
        if (palettes.isEmpty()) {
            return false;
        } else {
            List<StructureBlockInfo> list = pSettings.getRandomPalette(palettes, pOffset).blocks();
            if ((!list.isEmpty() || !pSettings.isIgnoreEntities() && !entityInfoList.isEmpty()) && size.getX() >= 1 && size.getY() >= 1 && size.getZ() >= 1) {
                BoundingBox boundingbox = pSettings.getBoundingBox();
                List<BlockPos> list1 = Lists.newArrayListWithCapacity(pSettings.shouldApplyWaterlogging() ? list.size() : 0);
                List<BlockPos> list2 = Lists.newArrayListWithCapacity(pSettings.shouldApplyWaterlogging() ? list.size() : 0);
                List<Pair<BlockPos, CompoundTag>> list3 = Lists.newArrayListWithCapacity(list.size());
                int i = Integer.MAX_VALUE;
                int j = Integer.MAX_VALUE;
                int k = Integer.MAX_VALUE;
                int l = Integer.MIN_VALUE;
                int i1 = Integer.MIN_VALUE;
                int j1 = Integer.MIN_VALUE;

                for(StructureBlockInfo structuretemplate$structureblockinfo : processBlockInfos(pServerLevel, pOffset, pPos, pSettings, list)) {
                    BlockPos blockpos = structuretemplate$structureblockinfo.pos();
                    if (boundingbox == null || boundingbox.isInside(blockpos)) {
                        FluidState fluidstate = pSettings.shouldApplyWaterlogging() ? pServerLevel.getFluidState(blockpos) : null;
                        BlockState blockstate = structuretemplate$structureblockinfo.state().mirror(pSettings.getMirror()).rotate(pSettings.getRotation());
                        if (structuretemplate$structureblockinfo.nbt() != null) {
                            BlockEntity blockentity = pServerLevel.getBlockEntity(blockpos);
                            Clearable.tryClear(blockentity);
                            pServerLevel.setBlock(blockpos, Blocks.BARRIER.defaultBlockState(), 20);
                        }

                        if (pServerLevel.setBlock(blockpos, blockstate, pFlags)) {
                            i = Math.min(i, blockpos.getX());
                            j = Math.min(j, blockpos.getY());
                            k = Math.min(k, blockpos.getZ());
                            l = Math.max(l, blockpos.getX());
                            i1 = Math.max(i1, blockpos.getY());
                            j1 = Math.max(j1, blockpos.getZ());
                            list3.add(Pair.of(blockpos, structuretemplate$structureblockinfo.nbt()));
                            if (structuretemplate$structureblockinfo.nbt() != null) {
                                BlockEntity blockentity1 = pServerLevel.getBlockEntity(blockpos);
                                if (blockentity1 != null) {
                                    if (blockentity1 instanceof RandomizableContainer) {
                                        structuretemplate$structureblockinfo.nbt().putLong("LootTableSeed", pRandom.nextLong());
                                    }
                                    try{
                                        blockentity1.loadWithComponents(structuretemplate$structureblockinfo.nbt(), pServerLevel.registryAccess());
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }
                            }

                            if (fluidstate != null) {
                                if (blockstate.getFluidState().isSource()) {
                                    list2.add(blockpos);
                                } else if (blockstate.getBlock() instanceof LiquidBlockContainer) {
                                    ((LiquidBlockContainer)blockstate.getBlock()).placeLiquid(pServerLevel, blockpos, blockstate, fluidstate);
                                    if (!fluidstate.isSource()) {
                                        list1.add(blockpos);
                                    }
                                }
                            }
                        }
                    }
                }

                boolean flag = true;
                Direction[] adirection = new Direction[]{Direction.UP, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};

                while(flag && !list1.isEmpty()) {
                    flag = false;
                    Iterator<BlockPos> iterator = list1.iterator();

                    while(iterator.hasNext()) {
                        BlockPos blockpos3 = (BlockPos)iterator.next();
                        FluidState fluidstate2 = pServerLevel.getFluidState(blockpos3);

                        for(int i2 = 0; i2 < adirection.length && !fluidstate2.isSource(); ++i2) {
                            BlockPos blockpos1 = blockpos3.relative(adirection[i2]);
                            FluidState fluidstate1 = pServerLevel.getFluidState(blockpos1);
                            if (fluidstate1.isSource() && !list2.contains(blockpos1)) {
                                fluidstate2 = fluidstate1;
                            }
                        }

                        if (fluidstate2.isSource()) {
                            BlockState blockstate1 = pServerLevel.getBlockState(blockpos3);
                            Block block = blockstate1.getBlock();
                            if (block instanceof LiquidBlockContainer) {
                                ((LiquidBlockContainer)block).placeLiquid(pServerLevel, blockpos3, blockstate1, fluidstate2);
                                flag = true;
                                iterator.remove();
                            }
                        }
                    }
                }

                if (i <= l) {
                    if (!pSettings.getKnownShape()) {
                        DiscreteVoxelShape discretevoxelshape = new BitSetDiscreteVoxelShape(l - i + 1, i1 - j + 1, j1 - k + 1);
                        int k1 = i;
                        int l1 = j;
                        int j2 = k;

                        for(Pair<BlockPos, CompoundTag> pair1 : list3) {
                            BlockPos blockpos2 = (BlockPos)pair1.getFirst();
                            discretevoxelshape.fill(blockpos2.getX() - k1, blockpos2.getY() - l1, blockpos2.getZ() - j2);
                        }

                        updateShapeAtEdge(pServerLevel, pFlags, discretevoxelshape, k1, l1, j2);
                    }

                    for(Pair<BlockPos, CompoundTag> pair : list3) {
                        BlockPos blockpos4 = (BlockPos)pair.getFirst();
                        if (!pSettings.getKnownShape()) {
                            BlockState blockstate2 = pServerLevel.getBlockState(blockpos4);
                            BlockState blockstate3 = Block.updateFromNeighbourShapes(blockstate2, pServerLevel, blockpos4);
                            if (blockstate2 != blockstate3) {
                                pServerLevel.setBlock(blockpos4, blockstate3, pFlags & -2 | 16);
                            }

                            pServerLevel.blockUpdated(blockpos4, blockstate3.getBlock());
                        }

                        if (pair.getSecond() != null) {
                            BlockEntity blockentity2 = pServerLevel.getBlockEntity(blockpos4);
                            if (blockentity2 != null) {
                                blockentity2.setChanged();
                            }
                        }
                    }
                }
//
//                if (!pSettings.isIgnoreEntities()) {
//                    placeEntities(pServerLevel, pOffset, pSettings.getMirror(), pSettings.getRotation(), pSettings.getRotationPivot(), boundingbox, pSettings.shouldFinalizeEntities());
//                }

                return true;
            } else {
                return false;
            }
        }
    }

//    private void placeEntities(ServerLevelAccessor pServerLevel, BlockPos pPos, Mirror pMirror, Rotation pRotation, BlockPos pOffset, BoundingBox pBoundingBox, boolean pWithEntities){
//        StructureTemplateAccessor accessor = (StructureTemplateAccessor) this;
//        List<Palette> palettes = accessor.getPalettes();
//        List<StructureTemplate.StructureEntityInfo> entityInfoList = accessor.getEntityInfoList();
//        for (StructureTemplate.StructureEntityInfo structuretemplate$structureentityinfo : entityInfoList) {
//            BlockPos blockpos = transform(structuretemplate$structureentityinfo.blockPos, pMirror, pRotation, pOffset).offset(pPos);
//            if (pBoundingBox == null || pBoundingBox.isInside(blockpos)) {
//                CompoundTag compoundtag = structuretemplate$structureentityinfo.nbt.copy();
//                Vec3 vec3 = transform(structuretemplate$structureentityinfo.pos, pMirror, pRotation, pOffset);
//                Vec3 vec31 = vec3.add((double)pPos.getX(), (double)pPos.getY(), (double)pPos.getZ());
//                ListTag listtag = new ListTag();
//                listtag.add(DoubleTag.valueOf(vec31.x));
//                listtag.add(DoubleTag.valueOf(vec31.y));
//                listtag.add(DoubleTag.valueOf(vec31.z));
//                compoundtag.put("Pos", listtag);
//                compoundtag.remove("UUID");
//                createEntityIgnoreException(pServerLevel, compoundtag).ifPresent(p_275190_ -> {
//                    float f = p_275190_.rotate(pRotation);
//                    f += p_275190_.mirror(pMirror) - p_275190_.getYRot();
//                    p_275190_.moveTo(vec31.x, vec31.y, vec31.z, f, p_275190_.getXRot());
//                    if (pWithEntities && p_275190_ instanceof Mob) {
//                        ((Mob)p_275190_).finalizeSpawn(pServerLevel, pServerLevel.getCurrentDifficultyAt(BlockPos.containing(vec31)), MobSpawnType.STRUCTURE, null);
//                    }
//
//                    pServerLevel.addFreshEntityWithPassengers(p_275190_);
//                });
//            }
//        }
//    }

}
