package com.hollingsworth.schematic.common.util;

import com.hollingsworth.schematic.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Locale;

public class SchematicExport {
	public static final Path SCHEMATICS = Services.PLATFORM.getGameDirectory().resolve("schematics");

	public static StructureTemplate getStructure(Level level, BlockPos first, BlockPos second){
		BoundingBox bb = BoundingBox.fromCorners(first, second);
		BlockPos origin = new BlockPos(bb.minX(), bb.minY(), bb.minZ());
		BlockPos bounds = new BlockPos(bb.getXSpan(), bb.getYSpan(), bb.getZSpan());

		StructureTemplate structure = new StructureTemplate();
		structure.fillFromWorld(level, origin, bounds, true, Blocks.AIR);
		return structure;
	}

	/**
	 * Save a schematic to a file from a world.
	 * @param dir the directory the schematic will be created in
	 * @param fileName the ideal name of the schematic, may not be the name of the created file
	 * @param overwrite whether overwriting an existing schematic is allowed
	 * @param level the level where the schematic structure is placed
	 * @param first the first corner of the schematic area
	 * @param second the second corner of the schematic area
	 * @return a SchematicExportResult, or null if an error occurred.
	 */
	@Nullable
	public static SchematicExportResult saveSchematic(Path dir, String fileName, boolean overwrite, Level level, BlockPos first, BlockPos second) {
		BoundingBox bb = BoundingBox.fromCorners(first, second);
		BlockPos origin = new BlockPos(bb.minX(), bb.minY(), bb.minZ());
		BlockPos bounds = new BlockPos(bb.getXSpan(), bb.getYSpan(), bb.getZSpan());

		StructureTemplate structure = getStructure(level, first, second);
		CompoundTag data = structure.save(new CompoundTag());
		String air = "minecraft:air";
		String structureVoid = "minecraft:structure_void";
		data.getList("palette", 10).forEach(inbt -> {
			CompoundTag c = (CompoundTag) inbt;
			if (c.contains("Name") && c.getString("Name")
					.equals(structureVoid)) {
				c.putString("Name", air);
			}
		});
		if (fileName.isEmpty())
			fileName = "failed_fallback";
		if (!overwrite)
			fileName = findFirstValidFilename(fileName, dir, "nbt");
		if (!fileName.endsWith(".nbt"))
			fileName += ".nbt";
		Path file = dir.resolve(fileName).toAbsolutePath();

		try {
			Files.createDirectories(dir);
			boolean overwritten = Files.deleteIfExists(file);
			try (OutputStream out = Files.newOutputStream(file, StandardOpenOption.CREATE)) {
				NbtIo.writeCompressed(data, out);
			}
			return new SchematicExportResult(file, dir, fileName, overwritten, origin, bounds);
		} catch (IOException e) {
			System.out.println("An error occurred while saving schematic [" + fileName + "]");
			return null;
		}
	}

	public record SchematicExportResult(Path file, Path dir, String fileName, boolean overwritten, BlockPos origin, BlockPos bounds) {
	}

	public static String findFirstValidFilename(String name, Path folderPath, String extension) {
		int index = 0;
		String filename;
		Path filepath;
		do {
			filename = slug(name) + ((index == 0) ? "" : "_" + index) + "." + extension;
			index++;
			filepath = folderPath.resolve(filename);
		} while (Files.exists(filepath));
		return filename;
	}

	public static String slug(String name) {
		return name.toLowerCase(Locale.ROOT).replaceAll("\\W+", "_");
	}
}
