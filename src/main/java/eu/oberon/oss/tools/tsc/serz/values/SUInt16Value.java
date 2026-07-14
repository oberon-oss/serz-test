package eu.oberon.oss.tools.tsc.serz.values;


import eu.oberon.oss.tools.tsc.serz.DataType;
import eu.oberon.oss.tools.tsc.serz.util.BaseJaveTypesBufferReader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Represents a signed 16-bit integer value in the SERZ format.
 *
 * @param value the short value
 *
 * @author TigerLilly64
 * @since 1.0.0
 */
public record SUInt16Value(int value) implements DataValue<Integer> {

    /**
     * Validates the unsigned 16-bit integer value.
     *
     * @param value the int value to validate
     *
     * @throws IllegalArgumentException if the value is out of range
     * @since 1.0.0
     */
    public SUInt16Value {
        if (value < 0 || value > 65535) {
            throw new IllegalArgumentException("sUInt16 must be between 0 and 65535");
        }
    }


    /**
     * Creates an unsigned 16-bit integer value from its binary representation.
     *
     * @param bytes the bytes to convert
     *
     * @since 1.0.0
     */
    public SUInt16Value(byte[] bytes) {
        this(Short.toUnsignedInt(BaseJaveTypesBufferReader.readShort(bytes, ByteOrder.LITTLE_ENDIAN)));
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0
     */
    @Override
    public DataType type() {
        return DataType.S_UINT16;
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0
     */
    @Override
    public byte[] toBytes(Integer source) {
        if (source < 0 || source > 65535) {
            throw new IllegalArgumentException("sUInt16 must be between 0 and 65535");
        }
        return ByteBuffer.allocate(Short.BYTES)
                .putShort((short) (source & 0xFFFF))
                .array();
    }
}
