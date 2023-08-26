package com.hollingsworth.schematic.data;

import com.google.common.collect.ImmutableList;
import com.hollingsworth.schematic.SchematicMod;
import com.hollingsworth.schematic.common.block.CafeBlocks;
import com.hollingsworth.schematic.common.block.RegistryWrapper;
import com.mojang.datafixers.util.Pair;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.LocationCheck;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class DefaultTableProvider extends LootTableProvider {
    public DefaultTableProvider(DataGenerator dataGeneratorIn) {
        super(dataGeneratorIn);
    }

    private static final float[] DEFAULT_SAPLING_DROP_RATES = new float[]{0.05F, 0.0625F, 0.083333336F, 0.1F};

    public static class BlockLootTable extends BlockLoot {
        public List<Block> list = new ArrayList<>();

        @Override
        protected void addTables() {
            registerDropSelf(CafeBlocks.CASH_REGISTER);

        }

        protected void registerSlabItemTable(Block p_124291_) {
            list.add(p_124291_);
            this.add(p_124291_,LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                    .add(applyExplosionDecay(p_124291_, LootItem.lootTableItem(p_124291_).apply(SetItemCountFunction.setCount(ConstantValue.exactly(2.0F))
                            .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(p_124291_).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SlabBlock.TYPE, SlabType.DOUBLE))))))));

        }

        protected <T extends Comparable<T> & StringRepresentable> void registerBedCondition(Block block, Property<T> prop, T isValue) {
            list.add(block);
            this.add(block, LootTable.lootTable().withPool(applyExplosionCondition(block, LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                    .add(LootItem.lootTableItem(block).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(prop, isValue)))))));
        }

        public void registerLeavesAndSticks(Block leaves, Block sapling) {
            list.add(leaves);
            this.add(leaves, l_state -> createLeavesDrops(l_state, sapling, DEFAULT_SAPLING_DROP_RATES));
        }

        public void registerDropDoor(Block block) {
            list.add(block);
            this.add(block, BlockLoot::createDoorTable);
        }

        public void registerDropSelf(RegistryWrapper block) {
            list.add((Block) block.get());
            dropSelf((Block) block.get());
        }

        public void registerDropSelf(Block block) {
            list.add(block);
            dropSelf(block);
        }

        public void registerDropSelf(RegistryObject<Block> block) {
            list.add(block.get());
            dropSelf(block.get());
        }
        public void registerDrop(Block input, ItemLike output) {
            list.add(input);
            dropOther(input, output);
        }

        @Override
        protected Iterable<Block> getKnownBlocks() {
            return list;
        }

    }

    public static class TipLoot implements Consumer<BiConsumer<ResourceLocation, LootTable.Builder>> {
        public static final LootItemCondition.Builder IN_JUNGLE = LocationCheck.checkLocation(LocationPredicate.Builder.location().setBiome(Biomes.JUNGLE));
        public static final LootItemCondition.Builder IN_SPARSE_JUNGLE = LocationCheck.checkLocation(LocationPredicate.Builder.location().setBiome(Biomes.SPARSE_JUNGLE));
        public static final LootItemCondition.Builder IN_BAMBOO_JUNGLE = LocationCheck.checkLocation(LocationPredicate.Builder.location().setBiome(Biomes.BAMBOO_JUNGLE));
        public static final ResourceLocation TIPPING_LOOT = new ResourceLocation(SchematicMod.MODID, "cafe/tipping");

        public void accept(BiConsumer<ResourceLocation, LootTable.Builder> consumer) {
//            consumer.accept(BuiltInLootTables.FISHING, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootTableReference.lootTableReference(BuiltInLootTables.FISHING_JUNK).setWeight(10).setQuality(-2)).add(LootTableReference.lootTableReference(BuiltInLootTables.FISHING_TREASURE).setWeight(5).setQuality(2).when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, EntityPredicate.Builder.entity().subPredicate(FishingHookPredicate.inOpenWater(true))))).add(LootTableReference.lootTableReference(BuiltInLootTables.FISHING_FISH).setWeight(85).setQuality(-1))));
//            consumer.accept(BuiltInLootTables.FISHING_FISH, LootTable.lootTable().withPool(LootPool.lootPool().add(LootItem.lootTableItem(Items.COD).setWeight(60)).add(LootItem.lootTableItem(Items.SALMON).setWeight(25)).add(LootItem.lootTableItem(Items.TROPICAL_FISH).setWeight(2)).add(LootItem.lootTableItem(Items.PUFFERFISH).setWeight(13))));
//            consumer.accept(BuiltInLootTables.FISHING_JUNK, LootTable.lootTable().withPool(LootPool.lootPool().add(LootItem.lootTableItem(Blocks.LILY_PAD).setWeight(17)).add(LootItem.lootTableItem(Items.LEATHER_BOOTS).setWeight(10).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.0F, 0.9F)))).add(LootItem.lootTableItem(Items.LEATHER).setWeight(10)).add(LootItem.lootTableItem(Items.BONE).setWeight(10)).add(LootItem.lootTableItem(Items.POTION).setWeight(10).apply(SetPotionFunction.setPotion(Potions.WATER))).add(LootItem.lootTableItem(Items.STRING).setWeight(5)).add(LootItem.lootTableItem(Items.FISHING_ROD).setWeight(2).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.0F, 0.9F)))).add(LootItem.lootTableItem(Items.BOWL).setWeight(10)).add(LootItem.lootTableItem(Items.STICK).setWeight(5)).add(LootItem.lootTableItem(Items.INK_SAC).setWeight(1).apply(SetItemCountFunction.setCount(ConstantValue.exactly(10.0F)))).add(LootItem.lootTableItem(Blocks.TRIPWIRE_HOOK).setWeight(10)).add(LootItem.lootTableItem(Items.ROTTEN_FLESH).setWeight(10)).add(LootItem.lootTableItem(Blocks.BAMBOO).when(IN_JUNGLE.or(IN_SPARSE_JUNGLE).or(IN_BAMBOO_JUNGLE)).setWeight(10))));
//            consumer.accept(BuiltInLootTables.FISHING_TREASURE, LootTable.lootTable().withPool(LootPool.lootPool().add(LootItem.lootTableItem(Items.NAME_TAG)).add(LootItem.lootTableItem(Items.SADDLE)).add(LootItem.lootTableItem(Items.BOW).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.0F, 0.25F))).apply(EnchantWithLevelsFunction.enchantWithLevels(ConstantValue.exactly(30.0F)).allowTreasure())).add(LootItem.lootTableItem(Items.FISHING_ROD).apply(SetItemDamageFunction.setDamage(UniformGenerator.between(0.0F, 0.25F))).apply(EnchantWithLevelsFunction.enchantWithLevels(ConstantValue.exactly(30.0F)).allowTreasure())).add(LootItem.lootTableItem(Items.BOOK).apply(EnchantWithLevelsFunction.enchantWithLevels(ConstantValue.exactly(30.0F)).allowTreasure())).add(LootItem.lootTableItem(Items.NAUTILUS_SHELL))));
        }
    }



    private final List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> tables = ImmutableList.of(
            Pair.of(BlockLootTable::new, LootContextParamSets.BLOCK),
            Pair.of(TipLoot::new, LootContextParamSets.EMPTY)
    );

    @Override
    protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> getTables() {
        return tables;
    }

    @Override
    protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext validationtracker) {
        map.forEach((p_218436_2_, p_218436_3_) -> {
            LootTables.validate(validationtracker, p_218436_2_, p_218436_3_);
        });
    }

}
