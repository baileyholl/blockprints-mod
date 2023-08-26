package com.hollingsworth.schematic.common.block;

import com.hollingsworth.schematic.SchematicMod;
import com.hollingsworth.schematic.common.lib.BlockNames;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class CafeBlocks {
    public static final DeferredRegister<Block> BLOCK_REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, SchematicMod.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, SchematicMod.MODID);
    public static final DeferredRegister<Item> BLOCK_ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, SchematicMod.MODID);

    public static final RegistryWrapper<CashRegister> CASH_REGISTER = registerBlock(BlockNames.CASH_REGISTER, () -> new CashRegister(defaultProperties().noOcclusion()));

    public static final RegistryObject<BlockEntityType<CashRegisterEntity>> CASH_REGISTER_ENTITY = BLOCK_ENTITIES.register(BlockNames.CASH_REGISTER, () -> BlockEntityType.Builder.of(CashRegisterEntity::new, CASH_REGISTER.get()).build(null));
    public static final RegistryObject<BlockItem> CASH_REGISTER_ITEM = BLOCK_ITEMS.register(BlockNames.CASH_REGISTER, () -> new BlockItem(CASH_REGISTER.get(), new Item.Properties().tab(SchematicMod.TAB)));

    public static BlockBehaviour.Properties defaultProperties() {
        return BlockBehaviour.Properties.of(Material.STONE).sound(SoundType.STONE).strength(2.0f, 6.0f);
    }

    static RegistryWrapper registerBlock(String name, Supplier<Block> blockSupp) {
        return new RegistryWrapper<>(BLOCK_REGISTRY.register(name, blockSupp));
    }
}
