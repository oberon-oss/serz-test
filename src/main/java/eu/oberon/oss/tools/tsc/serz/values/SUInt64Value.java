package eu.oberon.oss.tools.tsc.serz.values;


import eu.oberon.oss.tools.tsc.serz.DataType;
import eu.oberon.oss.tools.tsc.serz.util.BaseJaveTypesBufferReader;

import java.math.BigInteger;
import java.nio.ByteOrder;

/**
 * Represents an unsigned 64-bit integer value in the SERZ format.
 *
 * @param value the long value (representing an unsigned 64-bit integer)
 *
 * @author TigerLilly64
 * @since 1.0.0
 */
public record SUInt64Value(BigInteger value) implements DataValue<BigInteger> {
    private static final BigInteger MAX_VALUE = new BigInteger("18446744073709551615");

    /**
     * Validates the unsigned 64-bit integer value.
     *
     * @param value the unsigned 64-bit integer value to validate
     *
     * @throws IllegalArgumentException if the value is out of range
     * @since 1.0.0
     */
    public SUInt64Value {
        if (value.signum() < 0 || value.compareTo(MAX_VALUE) > 0) {
            throw new IllegalArgumentException("sUInt64 must be between 0 and 18.446.744.073.709.551.615");
        }
    }

    /**
     * Creates an unsigned 64-bit integer value from its binary representation.
     *
     * @param bytes the bytes to convert
     *
     * @since 1.0.0
     */
    public SUInt64Value(byte[] bytes) {
        this(BaseJaveTypesBufferReader.readUnsignedLong(bytes, ByteOrder.LITTLE_ENDIAN));
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0
     */
    @Override
    public DataType type() {
        return DataType.S_UINT64;
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0
     */
    @Override
    public byte[] toBytes(BigInteger source) {
        if (source.signum() < 0 || source.compareTo(MAX_VALUE) > 0) {
            throw new IllegalArgumentException("sUInt64 must be between 0 and 18.446.744.073.709.551.615");
        }

        byte[] bytes = source.toByteArray();
        byte[] result = new byte[Long.BYTES];

        int sourcePosition = Math.max(0, bytes.length - Long.BYTES);
        int length = Math.min(bytes.length, Long.BYTES);
        System.arraycopy(bytes, sourcePosition, result, Long.BYTES - length, length);

        return result;
    }
}
