package com.hollingsworth.schematic.common.util;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.BiPredicate;

public class BlockUtil {
    /**
     * Max depth of the floor check to avoid endless void searching (Stackoverflow).
     */
    private static final int MAX_DEPTH = 50;

    /**
     * Amount of string required to try to calculate a blockpos.
     */
    private static final int BLOCKPOS_LENGTH = 3;

    /**
     * Selects a solid position with air above
     */
    public static final BiPredicate<BlockGetter, BlockPos> SOLID_AIR_POS_SELECTOR = (world, pos) -> {
        return (world.getBlockState(pos).canOcclude() || world.getBlockState(pos).getMaterial().isLiquid()) && world.getBlockState(
                pos.above()).getMaterial() == Material.AIR && world.getBlockState(pos.above(2)).getMaterial() == Material.AIR;
    };

    /**
     * Selects a double air position
     */
    public static final BiPredicate<BlockGetter, BlockPos> DOUBLE_AIR_POS_SELECTOR = (world, pos) -> {
        return world.getBlockState(pos).getMaterial() == Material.AIR && world.getBlockState(pos.above(1)).getMaterial() == Material.AIR;
    };

    public static BlockPos getRandomSpawn(BlockPos calcCenter, Level level) {

        // Get a random point on a circle around the colony,far out for the direction
        final int degree = level.random.nextInt(360);
        int x = (int) Math.round(20 * Math.cos(Math.toRadians(degree)));
        int z = (int) Math.round(20 * Math.sin(Math.toRadians(degree)));
        final BlockPos advanceTowards = calcCenter.offset(x, 0, z);

        BlockPos spawnPos = null;

        // 8 Tries
        for (int i = 0; i < 8; i++) {
            spawnPos = findSpawnPointInDirections(level, new BlockPos(calcCenter.getX(), calcCenter.getY(), calcCenter.getZ()), advanceTowards);
            if (spawnPos != null) {
                break;
            }
        }

        if (spawnPos == null) {
            return null;
        }


        return findAround(level, getFloor(spawnPos, level), 3, 30, SOLID_AIR_POS_SELECTOR);
    }

    public static Iterable<BlockPos> iterateAABB(@Nullable AABB pAabb){
        if(pAabb == null)
            return List.of();
        return BlockPos.betweenClosed(Mth.floor(pAabb.minX), Mth.floor(pAabb.minY), Mth.floor(pAabb.minZ), Mth.floor(pAabb.maxX), Mth.floor(pAabb.maxY), Mth.floor(pAabb.maxZ));
    }

    /**
     * Calculates the floor level.
     *
     * @param position input position.
     * @param world    the world the position is in.
     * @return returns BlockPos position with air above.
     */
    @NotNull
    public static BlockPos getFloor(@NotNull final BlockPos position, @NotNull final Level world) {
        final BlockPos floor = getFloor(new BlockPos.MutableBlockPos(position.getX(), position.getY(), position.getZ()), 0, world);
        if (floor == null) {
            return position;
        }
        return floor;
    }

    /**
     * Calculates the floor level.
     *
     * @param position input position.
     * @param depth    the iteration depth.
     * @param world    the world the position is in.
     * @return returns BlockPos position with air above.
     */
    @Nullable
    public static BlockPos getFloor(@NotNull final BlockPos.MutableBlockPos position, final int depth, @NotNull final Level world) {
        if (depth > MAX_DEPTH) {
            return null;
        }
        //If the position is floating in Air go downwards
        if (!solidOrLiquid(world, position)) {
            return getFloor(position.set(position.getX(), position.getY() - 1, position.getZ()), depth + 1, world);
        }
        //If there is no air above the block go upwards
        if (!solidOrLiquid(world, position.set(position.getX(), position.getY() + 1, position.getZ())) &&
                !solidOrLiquid(world, position.set(position.getX(), position.getY() + 2, position.getZ()))) {
            return position.immutable();
        }
        return getFloor(position.set(position.getX(), position.getY() + 1, position.getZ()), depth + 1, world);
    }

    /**
     * Finds a spawnpoint randomly in a circular shape around the center Advances
     *
     * @param start      the center of the area to search for a spawn point
     * @param advancePos The position we advance towards
     * @return the calculated position
     */
    private static BlockPos findSpawnPointInDirections(
            Level level,
            final BlockPos start,
            final BlockPos advancePos) {
        BlockPos spawnPos = new BlockPos(start);
        Vec3 tempPos = new Vec3(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());

        final int xDiff = Math.abs(start.getX() - advancePos.getX());
        final int zDiff = Math.abs(start.getZ() - advancePos.getZ());

        Vec3 xzRatio = new Vec3(xDiff * (start.getX() < advancePos.getX() ? 1 : -1), 0, zDiff * (start.getZ() < advancePos.getZ() ? 1 : -1));
        // Reduce ratio to 3 chunks a step
        xzRatio = xzRatio.normalize().scale(3);

        int validChunkCount = 0;
        for (int i = 0; i < 10; i++) {
            if (isEntityBlockLoaded(level, new BlockPos(tempPos))) {
                tempPos = tempPos.add(16 * xzRatio.x, 0, 16 * xzRatio.z);

                if (isEntityBlockLoaded(level, new BlockPos(tempPos))) {
                    spawnPos = new BlockPos(tempPos);
                    validChunkCount++;
                    if (validChunkCount > 5) {
                        return spawnPos;
                    }
                } else {
                    break;
                }
            } else {
                break;
            }
        }

        if (!spawnPos.equals(start)) {
            return spawnPos;
        }

        return null;
    }


    /**
     * Returns the first air position near the given start. Advances vertically first then horizontally
     *
     * @param start           start position
     * @param horizontalRange horizontal search range
     * @param verticalRange   vertical search range
     * @param predicate       check predicate for the right block
     * @return position or null
     */
    public static BlockPos findAround(final Level world, final BlockPos start, final int verticalRange, final int horizontalRange, final BiPredicate<BlockGetter, BlockPos> predicate) {
        if (horizontalRange < 1 && verticalRange < 1) {
            return null;
        }

        if (predicate.test(world, start)) {
            return start;
        }

        BlockPos temp;
        int y = 0;
        int y_offset = 1;

        for (int i = 0; i < verticalRange + 2; i++) {
            for (int steps = 1; steps <= horizontalRange; steps++) {
                // Start topleft of middle point
                temp = start.offset(-steps, y, -steps);

                // X ->
                for (int x = 0; x <= steps; x++) {
                    temp = temp.offset(1, 0, 0);
                    if (predicate.test(world, temp)) {
                        return temp;
                    }
                }

                // X
                // |
                // v
                for (int z = 0; z <= steps; z++) {
                    temp = temp.offset(0, 0, 1);
                    if (predicate.test(world, temp)) {
                        return temp;
                    }
                }

                // < - X
                for (int x = 0; x <= steps; x++) {
                    temp = temp.offset(-1, 0, 0);
                    if (predicate.test(world, temp)) {
                        return temp;
                    }
                }

                // ^
                // |
                // X
                for (int z = 0; z <= steps; z++) {
                    temp = temp.offset(0, 0, -1);
                    if (predicate.test(world, temp)) {
                        return temp;
                    }
                }
            }

            y += y_offset;
            y_offset = y_offset > 0 ? y_offset + 1 : y_offset - 1;
            y_offset *= -1;

            if (!isInWorldHeight(start.getY() + y, world)) {
                return null;
            }
        }

        return null;
    }

    /**
     * Checks if the block is loaded for block access
     *
     * @param world world to use
     * @param pos   position to check
     * @return true if block is accessible/loaded
     */
    public static boolean isBlockLoaded(final LevelAccessor world, final BlockPos pos)
    {
        return isChunkLoaded(world, pos.getX() >> 4, pos.getZ() >> 4);
    }

    /**
     * Returns whether a chunk is fully loaded
     *
     * @param world world to check on
     * @param x     chunk position
     * @param z     chunk position
     * @return true if loaded
     */
    public static boolean isChunkLoaded(final LevelAccessor world, final int x, final int z)
    {
        if (world.getChunkSource() instanceof ServerChunkCache)
        {
            final ChunkHolder holder = ((ServerChunkCache) world.getChunkSource()).chunkMap.visibleChunkMap.get(ChunkPos.asLong(x, z));
            if (holder != null)
            {
                return holder.getFullChunkFuture().getNow(ChunkHolder.UNLOADED_LEVEL_CHUNK).left().isPresent();
            }

            return false;
        }
        return world.getChunk(x, z, ChunkStatus.FULL, false) != null;
    }


    /**
     * Returns whether a chunk is fully loaded
     *
     * @param world world to check on
     * @param pos   chunk position
     * @return true if loaded
     */
    public static boolean isChunkLoaded(final LevelAccessor world, final ChunkPos pos)
    {
        return isChunkLoaded(world, pos.x, pos.z);
    }

    /**
     * Checks if the block is loaded for ticking entities(not all chunks tick entities)
     *
     * @param world world to use
     * @param pos   position to check
     * @return true if block is accessible/loaded
     */
    public static boolean isEntityBlockLoaded(final LevelAccessor world, final BlockPos pos)
    {
        return isEntityChunkLoaded(world, pos.getX() >> 4, pos.getZ() >> 4);
    }

    /**
     * Returns whether an entity ticking chunk is loaded at the position
     *
     * @param world world to check on
     * @param x     chunk position
     * @param z     chunk position
     * @return true if loaded
     */
    public static boolean isEntityChunkLoaded(final LevelAccessor world, final int x, final int z)
    {
        return isEntityChunkLoaded(world, new ChunkPos(x, z));
    }

    /**
     * Returns whether an entity ticking chunk is loaded at the position
     *
     * @param world world to check on
     * @param pos   chunk position
     * @return true if loaded
     */
    public static boolean isEntityChunkLoaded(final LevelAccessor world, final ChunkPos pos)
    {
        if (world instanceof ServerLevel)
        {
            return isChunkLoaded(world, pos) && ((ServerLevel) world).isPositionEntityTicking(pos.getWorldPosition());
        }
        return isChunkLoaded(world, pos);
    }

    /**
     * Returns a dimensions max height
     *
     * @param dimensionType
     * @return
     */
    public static int getDimensionMaxHeight(final DimensionType dimensionType)
    {
        return dimensionType.logicalHeight() + dimensionType.minY();
    }

    /**
     * Returns a dimension min height
     *
     * @param dimensionType
     * @return
     */
    public static int getDimensionMinHeight(final DimensionType dimensionType)
    {
        return dimensionType.minY();
    }

    /**
     * Check if a given block y is within world bounds
     *
     * @param yBlock
     * @param world
     * @return
     */
    public static boolean isInWorldHeight(final int yBlock, final Level world)
    {
        final DimensionType dimensionType = world.dimensionType();
        return yBlock > getDimensionMinHeight(dimensionType) && yBlock < getDimensionMaxHeight(dimensionType);
    }

    /**
     * Checks if a blockPos in a world is solid or liquid.
     * <p>
     * Useful to find a suitable Place to stand. (avoid these blocks to find one)
     *
     * @param world    the world to look in
     * @param blockPos the blocks position
     * @return true if solid or liquid
     */
    public static boolean solidOrLiquid(@NotNull final Level world, @NotNull final BlockPos blockPos)
    {
        final Material material = world.getBlockState(blockPos).getMaterial();
        return material.isSolid()
                || material.isLiquid();
    }

    public static double boxDistance(AABB box1, AABB box2) {
        // Calculate the minimum distance between the two axis aligned bounding boxes
        double x = Math.max(0, Math.max(box1.minX - box2.maxX, box2.minX - box1.maxX));
        double y = Math.max(0, Math.max(box1.minY - box2.maxY, box2.minY - box1.maxY));
        double z = Math.max(0, Math.max(box1.minZ - box2.maxZ, box2.minZ - box1.maxZ));
        return Math.sqrt(x * x + y * y + z * z);
    }

    public static double distanceBetween(BlockPos blockPos, BlockPos blockPos2) {
        return Math.sqrt(Math.pow(blockPos.getX() - blockPos2.getX(), 2) + Math.pow(blockPos.getY() - blockPos2.getY(), 2) + Math.pow(blockPos.getZ() - blockPos2.getZ(), 2));
    }
}
