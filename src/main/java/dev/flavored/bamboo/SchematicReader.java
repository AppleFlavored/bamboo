package dev.flavored.bamboo;

import dev.flavored.bamboo.format.SpongeV1SchematicReader;
import net.kyori.adventure.nbt.BinaryTagIO;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

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
        if (version == 1 || version == 2) {
            new SpongeV1SchematicReader(builder).read(root);
        } else if (version == 3) {
            // TODO: Handle Sponge schematic V3
            throw new SchematicFormatException("Unsupported Sponge schematic version: " + version);
        } else {
            throw new SchematicFormatException("Unsupported schematic format: " + version);
        }

        return builder.build();
    }
}
