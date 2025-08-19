package dev.flavored.bamboo.format;

import dev.flavored.bamboo.SchematicFormatException;
import dev.flavored.bamboo.SchematicSink;
import net.kyori.adventure.nbt.CompoundBinaryTag;

public final class MCEditSchematicReader {
    private final SchematicSink sink;

    public MCEditSchematicReader(SchematicSink sink) {
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
    }
}
