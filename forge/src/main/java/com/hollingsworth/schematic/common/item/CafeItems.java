package com.hollingsworth.schematic.common.item;

import com.hollingsworth.schematic.Constants;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class CafeItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Constants.MOD_ID);
    public static final RegistryObject<DeedItem> DEED = register("deed", () -> new DeedItem(new Item.Properties()));
    public static final RegistryObject<Schematic> SCHEMATIC = register("schematic", () -> new Schematic("house"));

    public static RegistryObject register(String name, Supplier<? extends Item> item) {
        return ITEMS.register(name, item);
    }

}
