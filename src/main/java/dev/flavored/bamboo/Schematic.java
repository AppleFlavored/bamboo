package dev.flavored.bamboo;

import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.batch.RelativeBlockBatch;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Represents a schematic.
 * @param width The width of the schematic.
 * @param height The height of the schematic.
 * @param length The length of the schematic.
 * @param origin The origin of the schematic.
 * @param blocks The block data of the schematic.
 */
public record Schematic(int width, int height, int length, Point origin, List<Block> blocks) {

    /**
     * Creates a new {@link Builder} for {@link Schematic}.
     * @return A new schematic builder.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Paste the schematic (including air blocks) at the specified position in a given {@link Instance}.
     * Use {@link #paste(Instance, Point, boolean)} if you want to ignore air blocks.
     * @param instance The instance where the schematic will be pasted.
     * @param position The position where the schematic will be pasted.
     */
    public void paste(Instance instance, Point position) {
        paste(instance, position, false);
    }

    /**
     * Paste the schematic at the specified position in a given {@link Instance} with an option to ignore air blockData.
     * @param instance The instance where the schematic will be pasted.
     * @param position The position where the schematic will be pasted.
     * @param ignoreAir Whether to ignore air blockData or not. By default, this is false.
     */
    public void paste(Instance instance, Point position, boolean ignoreAir) {
        RelativeBlockBatch batch = new RelativeBlockBatch();
        for (int i = 0; i < blocks.size(); i++) {
            final Block block = blocks.get(i);
            if (ignoreAir && block.isAir()) {
                continue;
            }

            int y = i / (width * length);
            int z = i % (width * length) / width;
            int x = i % (width * length) % width;
            int chunkX = (position.blockX() + origin.blockX() + x) >> 4;
            int chunkZ = (position.blockZ() + origin.blockZ() + z) >> 4;
            instance.loadOptionalChunk(chunkX, chunkZ).thenRun(() -> batch.setBlock(x, y, z, block));
        }

        batch.apply(instance, position.add(origin), null);
    }

    /**
     * A builder for {@link Schematic}.
     */
    public static class Builder implements SchematicSink {
        private int width;
        private int height;
        private int length;
        private Point origin = Pos.ZERO;
        private List<Block> blocks;

        private Builder() {
        }

        @Override
        public void origin(int x, int y, int z) {
            this.origin = new Pos(x, y, z);
        }

        @Override
        public void size(int width, int height, int length) {
            this.width = width;
            this.height = height;
            this.length = length;
        }

        @Override
        public void block(@NotNull Block block) {
            // no-op
        }

        @Override
        public void blocks(@NotNull List<Block> blocks) {
            this.blocks = blocks;
        }

        public Schematic build() {
            return new Schematic(width, height, length, origin, blocks);
        }
    }
}
