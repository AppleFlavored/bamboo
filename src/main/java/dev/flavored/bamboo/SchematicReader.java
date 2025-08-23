package dev.flavored.bamboo;

import dev.flavored.bamboo.format.MCEditSchematicReader;
import dev.flavored.bamboo.format.SpongeSchematicReader;
import net.kyori.adventure.nbt.BinaryTagIO;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * A {@link SchematicReader} reads Sponge (version 1 and 2) schematics.
 */
public class SchematicReader {

    /**
     * Initializes a new schematic reader.
     */
    public SchematicReader() {
    }

    /**
     * Reads a schematic from the given {@link Path}. It is assumed that the schematic data is compressed with GZIP.
     * @param path The path to the schematic file.
     * @return The schematic.
     * @throws SchematicFormatException If the schematic is invalid.
     * @throws IOException If an I/O error occurs while reading the file.
     */
    public @NotNull Schematic fromPath(@NotNull Path path) throws SchematicFormatException, IOException {
        return fromStream(Files.newInputStream(path));
    }

    /**
     * Reads a schematic from the given {@link InputStream}. It is assumed that the schematic data is compressed
     * with GZIP.
     * @param stream The input stream.
     * @return The schematic.
     * @throws SchematicFormatException If the schematic is invalid.
     * @throws IOException If an I/O error occurs while reading the stream.
     */
    public @NotNull Schematic fromStream(@NotNull InputStream stream) throws SchematicFormatException, IOException {
        CompoundBinaryTag root = BinaryTagIO.unlimitedReader().read(stream, BinaryTagIO.Compression.GZIP);
        return fromNBT(root);
    }

    /**
     * Reads a schematic from the given {@link CompoundBinaryTag}.
     * @param root The root tag.
     * @return The schematic.
     * @throws SchematicFormatException If the schematic is invalid.
     */
    public @NotNull Schematic fromNBT(@NotNull CompoundBinaryTag root) throws SchematicFormatException {
        Schematic.Builder builder = Schematic.builder();

        // Sponge V3 schematics have a nested "Schematic" root tag.
        CompoundBinaryTag nestedRoot = root.getCompound("Schematic");
        if (!nestedRoot.isEmpty()) {
            root = nestedRoot;
        }

        int version = root.getInt("Version", -1);
        if (version > 0) {
            new SpongeSchematicReader(builder, version).read(root);
        } else {
            new MCEditSchematicReader(builder).read(root);
        }

        return builder.build();
    }
}
