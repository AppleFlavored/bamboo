package dev.flavored.bamboo;

import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Represents a sink for data read from a schematic file. This interface can be used to receive information about the
 * schematic as it is being parsed.
 * Methods may be called in any order and some might not be called at all, depending on the schematic format.
 */
public interface SchematicSink {

    /**
     * Called when the offset of the schematic is known. The {@link SchematicReader} will prefer WorldEdit offsets to the
     * original offsets, if available.
     * @param x The x-coordinate of the offset in the schematic.
     * @param y The y-coordinate of the offset in the schematic.
     * @param z The z-coordinate of the offset in the schematic.
     */
    void offset(int x, int y, int z);

    /**
     * Called when the size of the schematic is known.
     * @param width The width (the size in the x-axis) of the schematic.
     * @param height The height (the size in the y-axis) of the schematic.
     * @param length The length (the size in the z-axis) of the schematic.
     */
    void size(int width, int height, int length);

    /**
     * Called after each block has been read from the schematic.
     * @param block The block that has been read from the schematic.
     */
    default void block(@NonNull Block block) {}

    /**
     * Called after the block data has been completely read from the schematic.
     * @param blocks The list of blocks in the schematic.
     */
    void blocks(@NonNull List<Block> blocks);

    /**
     * Called when the block entities in the schematic have been read.
     * @param blockEntities A map of block indices to their corresponding NBT data. -- Will change.
     */
    void blockEntities(@NonNull Map<Integer, CompoundBinaryTag> blockEntities);

    /**
     * Called when the schematic name is known. This will only be called if the schematic format has a name field.
     * @param name The name of the schematic.
     */
    default void name(@NotNull String name) {}

    /**
     * Called when the schematic author is known. This will only be called if the schematic format has an author field.
     * @param author The author of the schematic.
     */
    default void author(@NonNull String author) {}

    /**
     * Called when the date the schematic was created is known. This will only be called if the schematic format has a
     * creation date field.
     * @param createdAt The date and time when the schematic was created.
     */
    default void createdAt(@NotNull Instant createdAt) {}
}
