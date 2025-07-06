package dev.flavored.bamboo;

import net.minestom.server.instance.block.Block;
import org.jspecify.annotations.NonNull;

import java.util.List;

/**
 * Represents a sink for data read from a schematic file. This interface can be used to receive information about the
 * schematic as it is being parsed.
 * Methods may be called in any order and some might not be called at all, depending on the schematic format.
 */
public interface SchematicSink {

    /**
     * Sets the origin of the schematic. The origin is the point in the schematic that corresponds to the
     * @param x The x-coordinate of the origin in the schematic.
     * @param y The y-coordinate of the origin in the schematic.
     * @param z The z-coordinate of the origin in the schematic.
     */
    void origin(int x, int y, int z);

    /**
     * @param width The width (the size in the x-axis) of the schematic.
     * @param height The height (the size in the y-axis) of the schematic.
     * @param length The length (the size in the z-axis) of the schematic.
     */
    void size(int width, int height, int length);

    /**
     * Called after each block has been read from the schematic.
     *
     * @param block The block that has been read from the schematic.
     */
    void block(@NonNull Block block);

    /**
     * Called after the block data has been completely read from the schematic.
     * @param blocks The list of blocks in the schematic.
     */
    void blocks(@NonNull List<Block> blocks);
}
