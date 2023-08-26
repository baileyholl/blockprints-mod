package com.hollingsworth.schematic.data.advancement;

import com.hollingsworth.schematic.SchematicMod;
import net.minecraft.advancements.Advancement;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Consumer;

public class ANAdvancements implements Consumer<Consumer<Advancement>> {
    Consumer<Advancement> advCon;
    @Override
    public void accept(Consumer<Advancement> con) {
        this.advCon = con;

    }

    public ANAdvancementBuilder buildBasicItem(ItemLike item, Advancement parent){
        return builder(ForgeRegistries.ITEMS.getKey(item.asItem()).getPath()).normalItemRequirement(item).parent(parent);
    }

    public Advancement saveBasicItem(ItemLike item, Advancement parent){
        return buildBasicItem(item, parent).save(advCon);
    }

    public ANAdvancementBuilder builder(String key){
        return ANAdvancementBuilder.builder(SchematicMod.MODID, key);
    }
}
