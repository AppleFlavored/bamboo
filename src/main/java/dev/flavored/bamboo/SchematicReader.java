package dev.flavored.bamboo;

import net.kyori.adventure.nbt.BinaryTagIO;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.block.Block;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class SchematicReader {

    public SchematicReader() {
    }

    public Schematic fromPath(Path path) throws IOException {
        return fromStream(Files.newInputStream(path));
    }

    public Schematic fromStream(InputStream stream) throws IOException {
        CompoundBinaryTag root = BinaryTagIO.unlimitedReader().read(stream, BinaryTagIO.Compression.GZIP);
        return fromNBT(root);
    }

    public Schematic fromNBT(CompoundBinaryTag root) throws IOException {
        Schematic.Builder builder = Schematic.builder();

        short width = root.getShort("Width");
        short height = root.getShort("Height");
        short length = root.getShort("Length");

        if (width < 0) throw new IOException("Schematic is missing a width value");
        builder.width(width);

        if (height < 0) throw new IOException("Schematic is missing a height value");
        builder.height(height);

        if (length < 0) throw new IOException("Schematic is missing a length value");
        builder.length(length);

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
            int propertyListStart = key.indexOf('[');
            if (propertyListStart < 0) {
                indexToBlockMap.put(palette.getInt(key), Block.fromNamespaceId(key));
                continue;
            }

            int propertyListEnd = key.indexOf(']');
            String[] pairs = key.substring(propertyListStart + 1, propertyListEnd).split(",");
            Map<String, String> properties = new HashMap<>();
            for (String pair : pairs) {
                int equalsIndex = pair.indexOf('=');
                properties.put(pair.substring(0, equalsIndex), pair.substring(equalsIndex + 1));
            }

            Block block = Objects.requireNonNull(Block.fromNamespaceId(key.substring(0, propertyListStart)));
            indexToBlockMap.put(palette.getInt(key), block.withProperties(properties));
        }

        ArrayList<Block> blocks = new ArrayList<>();
        VarIntIterator iterator = new VarIntIterator(blockData);
        while (iterator.hasNext()) {
            int index = iterator.nextInt();
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
