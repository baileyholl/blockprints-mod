package com.hollingsworth.schematic.data;

import com.google.gson.JsonObject;
import com.hollingsworth.schematic.data.patchouli.IPatchouliPage;
import com.hollingsworth.schematic.data.patchouli.PatchouliBuilder;
import com.hollingsworth.schematic.data.patchouli.TextPage;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.RegistryObject;
import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static com.hollingsworth.schematic.data.RegistryHelper.getRegistryName;

public class PatchouliProvider implements DataProvider {
    public final DataGenerator generator;


    public List<PatchouliPage> pages = new ArrayList<>();

    public PatchouliProvider(DataGenerator generatorIn) {
        this.generator = generatorIn;
    }

    public void addEntries() {

    }

    public String getLangPath(String name, int count) {
        return "ars_nouveau.page" + count + "." + name;
    }

    public String getLangPath(String name) {
        return "ars_nouveau.page." + name;
    }

    public PatchouliPage addPage(PatchouliBuilder builder, Path path) {
        return addPage(new PatchouliPage(builder, path));
    }

    public PatchouliPage addPage(PatchouliPage patchouliPage){
        this.pages.add(patchouliPage);
        return patchouliPage;
    }

    public PatchouliBuilder buildBasicItem(ItemLike item, ResourceLocation category, IPatchouliPage recipePage) {
        PatchouliBuilder builder = new PatchouliBuilder(category, item.asItem().getDescriptionId())
                .withIcon(item.asItem())
                .withPage(new TextPage("cafetier.page." + getRegistryName(item.asItem()).getPath()));
        if (recipePage != null) {
            builder.withPage(recipePage);
        }
        return builder;
    }

    public PatchouliPage addBasicItem(ItemLike item, ResourceLocation category, IPatchouliPage recipePage) {
        PatchouliBuilder builder = buildBasicItem(item, category, recipePage);
        return addPage(new PatchouliPage(builder, getPath(category, getRegistryName(item.asItem()))));
    }

    public void addBasicItem(RegistryObject<? extends ItemLike> item, ResourceLocation category, IPatchouliPage recipePage) {
        addBasicItem(item.get(), category, recipePage);
    }

    public Path getPath(ResourceLocation category, ResourceLocation fileName) {
        return this.generator.getOutputFolder().resolve("data/ars_nouveau/patchouli_books/worn_notebook/en_us/entries/" + category.getPath() + "/" + fileName.getPath() + ".json");
    }

    public Path getPath(ResourceLocation category, String fileName) {
        return this.generator.getOutputFolder().resolve("data/ars_nouveau/patchouli_books/worn_notebook/en_us/entries/" + category.getPath() + "/" + fileName + ".json");
    }

    @Override
    public void run(CachedOutput cache) throws IOException {
        addEntries();
        for (PatchouliPage patchouliPage : pages) {
            DataProvider.saveStable(cache, patchouliPage.build(), patchouliPage.path);
        }
    }

    public record PatchouliPage(PatchouliBuilder builder, Path path) {
        @Override
        public Path path() {
            return path;
        }

        public JsonObject build() {
            return builder.build();
        }

        public String relationPath(){
            String fileName = path.getFileName().toString();
            fileName = FilenameUtils.removeExtension(fileName);
            return builder.category.toString() + "/" + fileName;
        }
    }




    @Override
    public String getName() {
        return "Patchouli";
    }
}
