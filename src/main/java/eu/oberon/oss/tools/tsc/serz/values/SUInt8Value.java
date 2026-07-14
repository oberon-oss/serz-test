package eu.oberon.oss.tools.tsc.serz.values;


import eu.oberon.oss.tools.tsc.serz.DataType;

/**
 * Represents an unsigned 8-bit integer value in the SERZ format.
 *
 * @param value the short value (representing an unsigned 8-bit integer)
 *
 * @author TigerLilly64
 * @since 1.0.0
 */
public record SUInt8Value(short value) implements DataValue<Short> {
    /**
     * Validates the unsigned 8-bit integer value.
     *
     * @param value the short value to validate
     *
     * @throws IllegalArgumentException if the value is out of range
     * @since 1.0.0
     */
    public SUInt8Value {
        if (value < 0 || value > 255) {
            throw new IllegalArgumentException("sUInt8 must be between 0 and 255");
        }
    }

    /**
     * Creates an unsigned 8-bit integer value from its binary representation.
     *
     * @param bytes the bytes to convert
     *
     * @since 1.0.0
     */
    public SUInt8Value(byte[] bytes) {
        this((short) Byte.toUnsignedInt(bytes[0]));
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0
     */
    @Override
    public DataType type() {
        return DataType.S_UINT8;
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0
     */
    @Override
    public byte[] toBytes(Short source) {
        if (source < 0 || source > 255) {
            throw new IllegalArgumentException("sUInt8 must be between 0 and 255");
        }
        return new byte[]{source.byteValue()};
    }
}
