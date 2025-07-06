package dev.flavored.bamboo.format;

import dev.flavored.bamboo.SchematicFormatException;
import dev.flavored.bamboo.SchematicSink;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.minestom.server.instance.block.Block;

import java.util.*;

public class SpongeV1SchematicReader {
    private final SchematicSink sink;

    public SpongeV1SchematicReader(SchematicSink sink) {
        this.sink = sink;
    }

    public void read(CompoundBinaryTag root) throws SchematicFormatException {
        short width = root.getShort("Width", (short)-1);
        short height = root.getShort("Height", (short)-1);
        short length = root.getShort("Length", (short)-1);
        if (width < 0 || height < 0 || length < 0) {
            throw new SchematicFormatException("Schematic has missing dimensions (width=" + width + ", height=" + height + ", length=" + length + ")");
        }
        sink.size(width, height, length);

        int[] min = root.getIntArray("Offset", new int[3]);
        System.out.println("Offset: " + min[0] + ", " + min[1] + ", " + min[2]);

        CompoundBinaryTag metadataCompound = root.getCompound("Metadata");
        if (!metadataCompound.isEmpty()) {
            int weOffsetX = metadataCompound.getInt("WEOffsetX");
            int weOffsetY = metadataCompound.getInt("WEOffsetY");
            int weOffsetZ = metadataCompound.getInt("WEOffsetZ");
            sink.origin(min[0] - weOffsetX, min[1] - weOffsetY, min[2] - weOffsetZ);
        } else {
            sink.origin(min[0], min[1], min[2]);
        }

        int paletteMax = root.getInt("PaletteMax");
        CompoundBinaryTag palette = root.getCompound("Palette");
        if (palette.size() != paletteMax) {
            throw new SchematicFormatException("Palette size does not match expected size");
        }

        readBlockData(palette, root.getByteArray("BlockData"));
    }

    private void readTileEntities(CompoundBinaryTag list) {
    }

    private void readBlockData(CompoundBinaryTag palette, byte[] blockData) {
        Map<Integer, Block> indexToBlockMap = new HashMap<>(palette.size());
        for (String key : palette.keySet()) {
            int propertyListStart = key.indexOf('[');
            if (propertyListStart < 0) {
                indexToBlockMap.put(palette.getInt(key), Block.fromKey(key));
                continue;
            }

            int propertyListEnd = key.indexOf(']');
            String[] pairs = key.substring(propertyListStart + 1, propertyListEnd).split(",");
            Map<String, String> properties = new HashMap<>();
            for (String pair : pairs) {
                int equalsIndex = pair.indexOf('=');
                properties.put(pair.substring(0, equalsIndex), pair.substring(equalsIndex + 1));
            }

            Block block = Objects.requireNonNull(Block.fromKey(key.substring(0, propertyListStart)));
            indexToBlockMap.put(palette.getInt(key), block.withProperties(properties));
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
