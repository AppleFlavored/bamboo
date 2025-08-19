package dev.flavored.bamboo.format;

import dev.flavored.bamboo.SchematicFormatException;
import dev.flavored.bamboo.SchematicSink;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.adventure.nbt.ListBinaryTag;
import net.minestom.server.instance.block.Block;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public final class SpongeSchematicReader {
    private final SchematicSink sink;
    private final int version;

    public SpongeSchematicReader(SchematicSink sink, int version) {
        this.sink = sink;
        this.version = version;
    }

    public void read(CompoundBinaryTag root) throws SchematicFormatException {
        short width = root.getShort("Width", (short)-1);
        short height = root.getShort("Height", (short)-1);
        short length = root.getShort("Length", (short)-1);
        if (width < 0 || height < 0 || length < 0) {
            throw new SchematicFormatException("Schematic has missing dimensions (width=" + width + ", height=" + height + ", length=" + length + ")");
        }
        sink.size(width, height, length);

        CompoundBinaryTag metadata = root.getCompound("Metadata");
        if (!metadata.isEmpty()) {
            int weOffsetX = metadata.getInt("WEOffsetX");
            int weOffsetY = metadata.getInt("WEOffsetY");
            int weOffsetZ = metadata.getInt("WEOffsetZ");
            sink.offset(weOffsetX, weOffsetY, weOffsetZ);

            String name = metadata.getString("Name");
            if (!name.isEmpty()) {
                sink.name(name);
            }
            String author = metadata.getString("Author");
            if (!author.isEmpty()) {
                sink.author(author);
            }
            long date = metadata.getLong("Date", -1);
            if (date >= 0) {
                sink.createdAt(Instant.ofEpochMilli(date));
            }
        } else {
            int[] min = root.getIntArray("Offset", new int[3]);
            sink.offset(min[0], min[1], min[2]);
        }

        if (version == 3) {
            CompoundBinaryTag blockContainer = root.getCompound("Blocks");
            if (blockContainer.isEmpty()) {
                throw new SchematicFormatException("Schematic is missing non-empty block container");
            }
            readBlockData(blockContainer.getCompound("Palette"), blockContainer.getByteArray("Data"));
            readBlockEntities(blockContainer.getList("BlockEntities"));
        } else {
            int paletteMax = root.getInt("PaletteMax");
            CompoundBinaryTag palette = root.getCompound("Palette");
            if (palette.size() != paletteMax) {
                throw new SchematicFormatException("Palette size does not match expected size");
            }
            readBlockData(palette, root.getByteArray("BlockData"));
            readBlockEntities(root.getList(version == 2 ? "BlockEntities" : "TileEntities"));
        }
    }

    private void readBlockEntities(ListBinaryTag blockEntityList) {
        // TODO: Implement reading block entities
    }

    private void readBlockData(CompoundBinaryTag palette, byte[] blockData) {
        Map<Integer, Block> indexToBlockMap = new HashMap<>(palette.size());
        for (String key : palette.keySet()) {
            Block block = Block.fromState(key);
            indexToBlockMap.put(palette.getInt(key), block);
        }

        ArrayList<Block> blocks = new ArrayList<>(blockData.length);
        VarIntIterator iterator = new VarIntIterator(blockData);
        while (iterator.hasNext()) {
            int index = iterator.nextInt();
            Block block = indexToBlockMap.get(index);
            blocks.add(block);
            sink.block(block);
        }

        sink.blocks(blocks);
    }
}
