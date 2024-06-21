package com.hollingsworth.schematic.client.renderer;


import com.hollingsworth.schematic.export.Template;
import com.hollingsworth.schematic.mixin.StructureTemplateAccessor;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.*;
import java.util.stream.Collectors;

public class StatePos {
    public BlockState state;
    public BlockPos pos;

    public StatePos(BlockState state, BlockPos pos) {
        this.state = state;
        this.pos = pos;
    }

    public StatePos(CompoundTag compoundTag) {
        if (!compoundTag.contains("blockstate") || !compoundTag.contains("blockpos")) {
            this.state = null;
            this.pos = null;
        }
        this.state = NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), compoundTag.getCompound("blockstate"));
        this.pos = NbtUtils.readBlockPos(compoundTag.getCompound("blockpos"));
    }

    public StatePos(CompoundTag compoundTag, ArrayList<BlockState> blockStates) {
        if (!compoundTag.contains("blockstateshort") || !compoundTag.contains("blockpos")) {
            this.state = null;
            this.pos = null;
        }
        this.state = blockStates.get(compoundTag.getShort("blockstateshort"));
        this.pos = BlockPos.of(compoundTag.getLong("blockpos"));
    }

    public CompoundTag getTag() {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.put("blockstate", NbtUtils.writeBlockState(state));
        compoundTag.put("blockpos", NbtUtils.writeBlockPos(pos));
        return compoundTag;
    }

    public CompoundTag getTag(ArrayList<BlockState> blockStates) {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putShort("blockstateshort", (short) blockStates.indexOf(state));
        compoundTag.putLong("blockpos", pos.asLong());
        return compoundTag;
    }

    public static ArrayList<StatePos> listFrom(StructureTemplate template){
        var accessor = (StructureTemplateAccessor)template;
        var list = new ArrayList<StatePos>();
        var palettes = accessor.getPalettes();
        if(palettes.isEmpty()){
            return list;
        }
        var palette = palettes.get(0);
        for(StructureTemplate.StructureBlockInfo blockInfo : palette.blocks()){
            list.add(new StatePos(blockInfo.state(), blockInfo.pos()));
        }
        return list;
    }

    public static ArrayList<BlockState> getBlockStateMap(ArrayList<StatePos> list) {
        ArrayList<BlockState> blockStateMap = new ArrayList<>();
        for (StatePos statePos : list) {
            if (!blockStateMap.contains(statePos.state))
                blockStateMap.add(statePos.state);
        }
        return blockStateMap;
    }

    public static ArrayList<StatePos> rotate(ArrayList<StatePos> list, ArrayList<TagPos> tagListMutable, Rotation rotation) {
        ArrayList<StatePos> rotatedList = new ArrayList<>();
        if (list == null || list.isEmpty()) {
            return rotatedList;
        }
        boolean tags = !(tagListMutable == null || tagListMutable.isEmpty()); //If not empty or null, it has tags!

        Map<BlockPos, CompoundTag> tagMap = new HashMap<>();
        if (tags)
            tagMap = tagListMutable.stream().collect(Collectors.toMap(e -> e.pos, e -> e.tag));

        for (StatePos statePos : list) {
            BlockPos oldPos = statePos.pos;
            BlockState oldState = statePos.state;
            BlockState newState = oldState.rotate(rotation);
            BlockPos newPos = oldPos.rotate(rotation);

            if (tags && tagMap.get(statePos.pos) != null) {
                CompoundTag tempTag = tagMap.get(statePos.pos);
                tagMap.remove(statePos.pos);
                tagMap.put(newPos, tempTag);
            }

            rotatedList.add(new StatePos(newState, newPos));
        }

        if (tags) {
            tagListMutable.clear();
            for (Map.Entry<BlockPos, CompoundTag> entry : tagMap.entrySet())
                tagListMutable.add(new TagPos(entry.getValue(), entry.getKey()));
        }

        return rotatedList;
    }

    public static ListTag getBlockStateNBT(ArrayList<BlockState> blockStateMap) {
        ListTag listTag = new ListTag();
        for (BlockState blockState : blockStateMap) {
            listTag.add(NbtUtils.writeBlockState(blockState));
        }
        return listTag;
    }

    public static ArrayList<BlockState> getBlockStateMapFromNBT(ListTag listTag) {
        ArrayList<BlockState> blockStateMap = new ArrayList<>();
        for (int i = 0; i < listTag.size(); i++) {
            BlockState blockState = NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), listTag.getCompound(i));
            blockStateMap.add(blockState);
        }
        return blockStateMap;
    }

    @OnlyIn(Dist.CLIENT)
    public static Map<Template.ItemStackKey, Integer> getItemList(ArrayList<StatePos> list) {
        Map<Template.ItemStackKey, Integer> itemList = new Object2IntOpenHashMap<>();
        if (list == null || list.isEmpty())
            return itemList;
        for (StatePos statePos : list) {
            BlockPos blockPos = BlockPos.ZERO;
            Level level = Minecraft.getInstance().level;
            ItemStack cloneStack = statePos.state.getBlock().getCloneItemStack(level, blockPos, statePos.state);
            Template.ItemStackKey itemStackKey = new Template.ItemStackKey(cloneStack, true);
            if (!itemList.containsKey(itemStackKey)) //Todo Slabs, etc
                itemList.put(itemStackKey, 1);
            else
                itemList.put(itemStackKey, itemList.get(itemStackKey) + 1);
        }
        return itemList;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof StatePos) {
            return ((StatePos) obj).state.equals(this.state) && ((StatePos) obj).pos.equals(this.pos);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(state, pos);
    }
}