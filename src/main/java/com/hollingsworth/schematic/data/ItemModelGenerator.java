package com.hollingsworth.schematic.data;

import com.google.common.base.Preconditions;
import com.hollingsworth.schematic.SchematicMod;
import com.hollingsworth.schematic.common.block.RegistryWrapper;
import com.hollingsworth.schematic.common.lib.BlockNames;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

import static com.hollingsworth.schematic.data.RegistryHelper.getRegistryName;

public class ItemModelGenerator extends net.minecraftforge.client.model.generators.ItemModelProvider {
    public ItemModelGenerator(DataGenerator generator, String modid, ExistingFileHelper existingFileHelper) {
        super(generator, modid, existingFileHelper);
    }

    @Override
    protected void registerModels() {

//        getBuilder(LibBlockNames.STRIPPED_AWLOG_BLUE).parent(BlockStatesDatagen.getUncheckedModel(LibBlockNames.STRIPPED_AWLOG_BLUE));
//        blockAsItem(LibBlockNames.MENDOSTEEN_POD);
//        itemUnchecked(ItemsRegistry.ALCHEMISTS_CROWN);
//        stateUnchecked(LibBlockNames.POTION_DIFFUSER);
        stateUnchecked(BlockNames.PLATE_BLOCK);
        stateUnchecked(BlockNames.DISPLAY_CASE);
        stateUnchecked(BlockNames.OAK_CHAIR);

    }

    public void blockAsItem(String s){
        getBuilder("cafetier:" + s).parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", itemTexture(s));
    }

    public void blockAsItem(RegistryWrapper<? extends Block> block){
        getBuilder(block.getRegistryName()).parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", itemTexture(block.get()));
    }

    public void itemUnchecked(RegistryWrapper<? extends Item> item){
        getBuilder(item.getRegistryName()).parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", itemTexture(item.get()));

    }

    public void stateUnchecked(String name){
        getBuilder(name).parent(BlockStatesDatagen.getUncheckedModel(name));
    }


    @Override
    public String getName() {
        return "cafetier Item Models";
    }

    private ResourceLocation registryName(final Item item) {
        return Preconditions.checkNotNull(getRegistryName(item), "Item %s has a null registry name", item);
    }

    private ResourceLocation registryName(final Block item) {
        return Preconditions.checkNotNull(getRegistryName(item), "Item %s has a null registry name", item);
    }

    private ResourceLocation itemTexture(String item) {
        return new ResourceLocation(SchematicMod.MODID, "items" + "/" + item);
    }

    private ResourceLocation itemTexture(final Item item) {
        final ResourceLocation name = registryName(item);
        return new ResourceLocation(name.getNamespace(), "items" + "/" + name.getPath());
    }

    private ResourceLocation itemTexture(final Block item) {
        final ResourceLocation name = registryName(item);
        return new ResourceLocation(name.getNamespace(), "items" + "/" + name.getPath());
    }
}
