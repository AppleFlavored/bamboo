package dev.flavored.bamboo;

import net.kyori.adventure.nbt.BinaryTagIO;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.block.Block;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class SchematicImporter {

    public SchematicImporter() {
    }

    public Schematic fromStream(InputStream stream) throws IOException {
        CompoundBinaryTag root = BinaryTagIO.unlimitedReader().read(stream, BinaryTagIO.Compression.GZIP);
        Schematic.Builder builder = Schematic.builder();
        System.out.println(root);

        // TODO: Add check for required keys.
        builder.width(root.getShort("Width"));
        builder.height(root.getShort("Height"));
        builder.length(root.getShort("Length"));

        CompoundBinaryTag metadataCompound = root.getCompound("Metadata");
        if (metadataCompound.size() > 0) {
            int weOffsetX = metadataCompound.getInt("WEOffsetX");
            int weOffsetY = metadataCompound.getInt("WEOffsetY");
            int weOffsetZ = metadataCompound.getInt("WEOffsetZ");
            builder.offset(new Vec(weOffsetX, weOffsetY, weOffsetZ));
        }

        int paletteMax = root.getInt("PaletteMax");
        CompoundBinaryTag palette = root.getCompound("Palette");
        if (palette.size() != paletteMax) {
            throw new IOException("Palette size does not match expected size");
        }

        List<Block> blockData = readBlockData(palette, root.getByteArray("BlockData"));
        builder.blocks(blockData);

        return builder.build();
    }

    private List<Block> readBlockData(CompoundBinaryTag palette, byte[] blockData) {
        Map<Integer, Block> indexToBlockMap = new HashMap<>(palette.size());
        for (String key : palette.keySet()) {
            int value = palette.getInt(key);
            indexToBlockMap.put(value, Block.fromNamespaceId(key));
        }

        ArrayList<Block> blocks = new ArrayList<>();
        VarIntIterator iterator = new VarIntIterator(blockData);
        while (iterator.hasNext()) {
            int index = iterator.nextInt();
            // TODO: Block states?
            Block block = indexToBlockMap.get(index);
            blocks.add(block);
        }
        return blocks;
    }

    private static class VarIntIterator implements PrimitiveIterator.OfInt {
        private final byte[] data;
        private int offset = 0;

        public VarIntIterator(byte[] data) {
            this.data = data;
        }

        @Override
        public int nextInt() {
            int value = 0;
            int size = 0;
            while (true) {
                byte b = data[offset + size];
                value |= (b & 0x7F) << (size++ * 7);
                if ((b & 0x80) == 0) {
                    break;
                }
            }
            offset += size;
            return value;
        }

        @Override
        public boolean hasNext() {
            return data.length > offset;
        }
    }
}
