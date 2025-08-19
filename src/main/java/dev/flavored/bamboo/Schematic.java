package dev.flavored.bamboo;

import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.batch.RelativeBlockBatch;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Represents a schematic.
 * @param width The width of the schematic.
 * @param height The height of the schematic.
 * @param length The length of the schematic.
 * @param offset The offset of the schematic.
 * @param blocks The block data of the schematic.
 */
public record Schematic(
        int width,
        int height,
        int length,
        Point offset,
        List<Block> blocks,
        Map<Integer, CompoundBinaryTag> blockEntities,
        String name,
        String author,
        Instant createdAt
) {

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
            int chunkX = (position.blockX() + offset.blockX() + x) >> 4;
            int chunkZ = (position.blockZ() + offset.blockZ() + z) >> 4;
            instance.loadOptionalChunk(chunkX, chunkZ).thenRun(() -> batch.setBlock(x, y, z, block));
        }

        batch.apply(instance, position.add(offset), null);
    }

    /**
     * A builder for {@link Schematic}.
     */
    public static class Builder implements SchematicSink {
        private int width;
        private int height;
        private int length;
        private Point offset = Pos.ZERO;
        private List<Block> blocks;
        private Map<Integer, CompoundBinaryTag> blockEntities = Map.of();
        private String name = null;
        private String author = null;
        private Instant createdAt = Instant.EPOCH;

        private Builder() {
        }

        @Override
        public void offset(int x, int y, int z) {
            this.offset = new Pos(x, y, z);
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

        @Override
        public void blockEntities(@NonNull Map<Integer, CompoundBinaryTag> blockEntities) {
            this.blockEntities = blockEntities;
        }

        @Override
        public void name(@NotNull String name) {
            this.name = name;
        }

        @Override
        public void author(@NonNull String author) {
            this.author = author;
        }

        @Override
        public void createdAt(@NotNull Instant createdAt) {
            this.createdAt = createdAt;
        }

        public Schematic build() {
            return new Schematic(width, height, length, offset, blocks, blockEntities, name, author, createdAt);
        }
    }
}
