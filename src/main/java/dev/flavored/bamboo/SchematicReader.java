package dev.flavored.bamboo;

import net.kyori.adventure.nbt.BinaryTagIO;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * A {@link SchematicReader} reads a Sponge (v1) schematic.
 */
public class SchematicReader {

    /**
     * Initializes a new schematic reader.
     */
    public SchematicReader() {
    }

    /**
     * Reads a schematic from the given {@link Path}.
     * @param path The path to the schematic file.
     * @return The schematic.
     * @throws IOException If the schematic could not be read or is invalid.
     */
    public @NotNull Schematic fromPath(@NotNull Path path) throws IOException {
        return fromStream(Files.newInputStream(path));
    }

    /**
     * Reads a schematic from the given {@link InputStream}.
     * @param stream The input stream.
     * @return The schematic.
     * @throws IOException If the schematic could not be read or is invalid.
     */
    public @NotNull Schematic fromStream(@NotNull InputStream stream) throws IOException {
        CompoundBinaryTag root = BinaryTagIO.unlimitedReader().read(stream, BinaryTagIO.Compression.GZIP);
        return fromNBT(root);
    }

    /**
     * Reads a schematic from the given {@link CompoundBinaryTag}.
     * @param root The root tag.
     * @return The schematic.
     * @throws IOException If the schematic is invalid.
     */
    public @NotNull Schematic fromNBT(@NotNull CompoundBinaryTag root) throws IOException {
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
        }
        return blocks;
    }

}
