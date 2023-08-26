package com.hollingsworth.schematic.data;


import com.hollingsworth.schematic.common.block.CafeBlocks;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.level.ItemLike;

import java.util.function.Consumer;

public class RecipeDatagen extends RecipeProvider {
    public RecipeDatagen(DataGenerator generatorIn) {
        super(generatorIn);
    }

//    public static Ingredient SOURCE_GEM = Ingredient.of(ItemTagProvider.SOURCE_GEM_TAG);

    public Consumer<FinishedRecipe> consumer;

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {
        {
            this.consumer = consumer;


        }
    }


    public ShapedRecipeBuilder shapedBuilder(ItemLike item) {
        return shapedBuilder(item, 1);
    }

    public ShapedRecipeBuilder shapedBuilder(ItemLike result, int count) {
        return ShapedRecipeBuilder.shaped(result, count).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(CafeBlocks.CASH_REGISTER.get()));
    }

    public ShapelessRecipeBuilder shapelessBuilder(ItemLike result) {
        return shapelessBuilder(result, 1);
    }

    public ShapelessRecipeBuilder shapelessBuilder(ItemLike result, int resultCount) {
        return ShapelessRecipeBuilder.shapeless(result, resultCount).unlockedBy("has_journal", InventoryChangeTrigger.TriggerInstance.hasItems(CafeBlocks.CASH_REGISTER.get()));
    }
}
