package com.hollingsworth.schematic.common.util;

import com.hollingsworth.schematic.Constants;
import net.minecraft.core.HolderGetter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.zip.GZIPInputStream;

public class FileUtils {
    public static StructureTemplate loadStructureTemplate(HolderGetter<Block> lookup, Path file) {
        StructureTemplate t = new StructureTemplate();
        Path path = file.normalize();

        try (DataInputStream stream = new DataInputStream(new BufferedInputStream(
                new GZIPInputStream(Files.newInputStream(path, StandardOpenOption.READ))))) {
            CompoundTag nbt = NbtIo.read(stream, NbtAccounter.create(0x20000000L));
            t.load(lookup, nbt);
        } catch (IOException e) {
            Constants.LOG.warn("Failed to read schematic", e);
        }
        return t;
    }
}
