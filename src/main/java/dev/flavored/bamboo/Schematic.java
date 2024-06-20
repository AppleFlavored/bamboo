package dev.flavored.bamboo;

import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.batch.AbsoluteBlockBatch;
import net.minestom.server.instance.block.Block;

import java.util.List;

public record Schematic(short width, short height, short length, Point offset, List<Block> blocks) {

    public static Builder builder() {
        return new Builder();
    }

    public void paste(Instance instance, Point position) {
        AbsoluteBlockBatch batch = new AbsoluteBlockBatch();
        for (int i = 0; i < blocks.size(); i++) {
            final Block block = blocks.get(i);
            int y = i / (width * length);
            int z = i % (width * length) / width;
            int x = i % (width * length) % width;

            Point absolutePos = position.add(x + offset.x(), y + offset.y(), z + offset.z());
            // The chunk must be loaded before we can apply the patch.
            instance.loadOptionalChunk(absolutePos).thenRun(() -> batch.setBlock(absolutePos, block));
        }
        batch.apply(instance, null);
    }

    public static class Builder {
        private short width;
        private short height;
        private short length;
        private Point offset;
        private List<Block> blocks;

        private Builder() {
        }

        public Builder width(short width) {
            this.width = width;
            return this;
        }

        public Builder height(short height) {
            this.height = height;
            return this;
        }

        public Builder length(short length) {
            this.length = length;
            return this;
        }

        public Builder offset(Point offset) {
            this.offset = offset;
            return this;
        }

        public Builder blocks(List<Block> blockData) {
            this.blocks = blockData;
            return this;
        }

        public Schematic build() {
            return new Schematic(width, height, length, offset, blocks);
        }
    }
}
