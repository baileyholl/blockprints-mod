package com.hollingsworth.schematic.export;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hollingsworth.schematic.client.renderer.StatePos;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastBufferedInputStream;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

// Yoinked from building gadgets for json support
public class Template {
    public String name;
    public String statePosArrayList;
    public Map<String, Integer> requiredItems = new Object2IntOpenHashMap<>();

    public Template(String name, BlockPos start, BlockPos end) {
        this.name = name;
        ArrayList<StatePos> statePosArrayList = Template.listForDire(start, end, Minecraft.getInstance().level);
        this.statePosArrayList = statePosListToNBTMapArray(statePosArrayList).toString();
        Map<ItemStackKey, Integer> requiredItemsTemp = StatePos.getItemList(statePosArrayList);
        for (Map.Entry<ItemStackKey, Integer> entry : requiredItemsTemp.entrySet()) {
            if (entry.getKey().getStack().isEmpty()) continue;
            var item = entry.getKey().item;
            ResourceLocation registryName = BuiltInRegistries.ITEM.getKey(item.value());
            requiredItems.put(registryName.toString(), entry.getValue());
        }
    }

    public String toJson() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(this);
    }

    private static final ImmutableList<Property<?>> DENY_PROPERTIES = ImmutableList.of(
            BlockStateProperties.AGE_1, BlockStateProperties.AGE_2, BlockStateProperties.AGE_3, BlockStateProperties.AGE_4,
            BlockStateProperties.AGE_5, BlockStateProperties.AGE_7, BlockStateProperties.AGE_15, BlockStateProperties.AGE_25,
            DoublePlantBlock.HALF, BlockStateProperties.WATERLOGGED, BlockStateProperties.LIT, BlockStateProperties.HAS_RECORD,
            BlockStateProperties.HAS_BOOK, BlockStateProperties.OPEN, BlockStateProperties.STAGE
    );

    public static ArrayList<StatePos> listForDire(BlockPos startRaw, BlockPos endRaw, Level level){
        ArrayList<StatePos> list = new ArrayList<>();
        AABB area = AABB.encapsulatingFullBlocks(startRaw, endRaw);
        BlockPos.betweenClosedStream(area).map(BlockPos::immutable).forEach(pos -> {
            if (validStateForDire(level.getBlockState(pos), level, pos))
                list.add(new StatePos(cleanBlockState(level.getBlockState(pos)), pos.subtract(startRaw)));
            else
                list.add(new StatePos(Blocks.AIR.defaultBlockState(), pos.subtract(startRaw))); //We need to have a block in EVERY position, so write air if invalid
        });
        return list;
    }

    public static CompoundTag statePosListToNBTMapArray(ArrayList<StatePos> list) {
        CompoundTag tag = new CompoundTag();
        ArrayList<BlockState> blockStateMap = StatePos.getBlockStateMap(list);
        ListTag blockStateMapList = StatePos.getBlockStateNBT(blockStateMap);
        int[] blocklist = new int[list.size()];
        final int[] counter = {0};

        BlockPos start = list.get(0).pos;
        BlockPos end = list.get(list.size() - 1).pos;
        AABB aabb = new AABB(new Vec3(start.getX(), start.getY(), start.getZ()), new Vec3(end.getX(), end.getY(), end.getZ()));

        Map<BlockPos, BlockState> blockStateByPos = list.stream()
                .collect(Collectors.toMap(e -> e.pos, e -> e.state));

        BlockPos.betweenClosedStream(aabb).map(BlockPos::immutable).forEach(pos -> {
            BlockState blockState = blockStateByPos.get(pos);
            blocklist[counter[0]++] = blockStateMap.indexOf(blockState);
        });

        tag.put("startpos", NbtUtils.writeBlockPos(start));
        tag.put("endpos", NbtUtils.writeBlockPos(end));
        tag.put("blockstatemap", blockStateMapList);
        tag.putIntArray("statelist", blocklist); //Todo - Short Array?
        return tag;
    }
    public static BlockState cleanBlockState(BlockState sourceState) {
        BlockState placeState = sourceState.getBlock().defaultBlockState();
        for (Property<?> prop : sourceState.getProperties()) {
            if (!DENY_PROPERTIES.contains(prop)) {
                placeState = applyProperty(placeState, sourceState, prop);
            }
        }
        return placeState;
    }
    private static <T extends Comparable<T>> BlockState applyProperty(BlockState state, BlockState from, Property<T> prop) {
        return state.setValue(prop, from.getValue(prop));
    }

    public static boolean validStateForDire(BlockState blockState, Level level, BlockPos blockPos){
        if (blockState.getDestroySpeed(level, blockPos) < 0) return false;
        if (!blockState.getFluidState().isEmpty() && !blockState.getFluidState().isSource()) return false;
        return true;
    }

    public static ArrayList<StatePos> statePosListFromNBTMapArray(CompoundTag tag) {
        ArrayList<StatePos> statePosList = new ArrayList<>();
        if (!tag.contains("blockstatemap") || !tag.contains("statelist")) return statePosList;
        ArrayList<BlockState> blockStateMap = StatePos.getBlockStateMapFromNBT(tag.getList("blockstatemap", Tag.TAG_COMPOUND));
        BlockPos start = readBlockPos(tag, "startpos");
        BlockPos end = readBlockPos(tag, "endpos");
        AABB aabb = aabbFromBlockPos(start, end);
        int[] blocklist = tag.getIntArray("statelist");
        final int[] counter = {0};
        BlockPos.betweenClosedStream(aabb).map(BlockPos::immutable).forEach(pos -> {
            int blockStateLookup = blocklist[counter[0]++];
            BlockState blockState = blockStateMap.get(blockStateLookup);
            statePosList.add(new StatePos(blockState, pos));
        });
        return statePosList;
    }

    public static BlockPos readBlockPos(CompoundTag compoundTag, String pKey) {
        if (!compoundTag.contains(pKey)) return BlockPos.ZERO;
        CompoundTag tag = compoundTag.getCompound(pKey);
        return new BlockPos(tag.getInt("X"), tag.getInt("Y"), tag.getInt("Z"));
    }


    public static Vec3 blockPosToVec3(BlockPos pos) {
        return new Vec3(pos.getX(), pos.getY(), pos.getZ());
    }

    public static AABB aabbFromBlockPos(BlockPos start, BlockPos end) {
        return new AABB(blockPosToVec3(start), blockPosToVec3(end));
    }

    public static CompoundTag readCompressed(InputStream pZippedStream, NbtAccounter pAccounter) throws IOException {
        CompoundTag compoundtag;
        try (DataInputStream datainputstream = new DataInputStream(new FastBufferedInputStream(new GZIPInputStream(pZippedStream)))){
             compoundtag = NbtIo.read(datainputstream, pAccounter);
        }

        return compoundtag;
    }

    public static class ItemStackKey {
        public final Holder<Item> item;
        public final DataComponentPatch dataComponents;
        private final int hash;


        public ItemStackKey(ItemStack stack, boolean compareNBT) {
            this.item = stack.getItemHolder();
            this.dataComponents = compareNBT ? stack.getComponentsPatch() : DataComponentPatch.EMPTY;
            this.hash = Objects.hash(item, dataComponents);
        }

        public ItemStack getStack() {
            return new ItemStack(item, 1, dataComponents);
        }

        public ItemStack getStack(int amt) {
            return new ItemStack(item, amt, dataComponents);
        }

        @Override
        public int hashCode() {
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof ItemStackKey) {
                return (((ItemStackKey) obj).item == this.item) && Objects.equals(((ItemStackKey) obj).dataComponents, this.dataComponents);
            }
            return false;
        }
    }
}
