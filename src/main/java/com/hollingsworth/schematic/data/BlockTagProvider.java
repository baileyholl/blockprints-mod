package com.hollingsworth.schematic.data;

import com.hollingsworth.schematic.SchematicMod;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.nio.file.Path;

public class BlockTagProvider extends BlockTagsProvider {

    public static TagKey<Block> WAITING_BLOCK = BlockTags.create(new ResourceLocation(SchematicMod.MODID, "waiting_area"));

    private final DataGenerator generator;

    public BlockTagProvider(DataGenerator generatorIn, ExistingFileHelper helper) {
        super(generatorIn, SchematicMod.MODID, helper);
        this.generator = generatorIn;
    }

    @Override
    protected void addTags() {
        this.tag(WAITING_BLOCK).add(Blocks.LECTERN);
    }

    protected Path getPath(ResourceLocation p_126514_) {
        return this.generator.getOutputFolder().resolve("data/" + p_126514_.getNamespace() + "/tags/blocks/" + p_126514_.getPath() + ".json");
    }

    public String getName() {
        return "AN tags";
    }
}
