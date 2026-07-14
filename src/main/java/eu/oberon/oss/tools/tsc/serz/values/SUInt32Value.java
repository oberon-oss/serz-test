package eu.oberon.oss.tools.tsc.serz.values;


import eu.oberon.oss.tools.tsc.serz.DataType;
import eu.oberon.oss.tools.tsc.serz.util.BaseJaveTypesBufferReader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Represents an unsigned 32-bit integer value in the SERZ format.
 *
 * @param value the long value (representing an unsigned 32-bit integer)
 *
 * @author TigerLilly64
 * @since 1.0.0
 */
public record SUInt32Value(long value) implements DataValue<Long> {
    /**
     * Validates the unsigned 32-bit integer value.
     *
     * @param value the long value to validate
     *
     * @throws IllegalArgumentException if the value is out of range
     * @since 1.0.0
     */
    public SUInt32Value {
        if (value < 0 || value > 0xFFFF_FFFFL) {
            throw new IllegalArgumentException("sUInt32 must be between 0 and 4.294.967.295");
        }
    }

    /**
     * Creates an unsigned 32-bit integer value from its binary representation.
     *
     * @param bytes the bytes to convert
     *
     * @since 1.0.0
     */
    public SUInt32Value(byte[] bytes) {
        this(BaseJaveTypesBufferReader.readUnsignedInt(bytes, ByteOrder.LITTLE_ENDIAN));
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0
     */
    @Override
    public DataType type() {
        return DataType.S_UINT32;
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0
     */
    @Override
    public byte[] toBytes(Long source) {
        if (source < 0 || source > 0xFFFF_FFFFL) {
            throw new IllegalArgumentException("sUInt32 must be between 0 and 4.294.967.295");
        }
        return ByteBuffer.allocate(Integer.BYTES)
                .putInt((int) (source & 0xFFFF_FFFFL))
                .array();
    }
}
