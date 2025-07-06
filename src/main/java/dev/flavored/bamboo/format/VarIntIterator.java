package dev.flavored.bamboo.format;

import java.util.PrimitiveIterator;

class VarIntIterator implements PrimitiveIterator.OfInt {
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
