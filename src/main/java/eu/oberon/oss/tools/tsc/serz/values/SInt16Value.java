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
public record SInt16Value(short value) implements DataValue<Short> {

    /**
     * Creates a signed 16-bit integer value from its binary representation.
     *
     * @param bytes the bytes to convert
     *
     * @since 1.0.0
     */
    public SInt16Value(byte[] bytes) {
        this(BaseJaveTypesBufferReader.readShort(bytes, ByteOrder.LITTLE_ENDIAN));
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0
     */
    @Override
    public DataType type() {
        return DataType.S_INT16;
    }

    @Override
    public byte[] toBytes(Short source) {
        return ByteBuffer.allocate(Short.BYTES)
                .order(ByteOrder.nativeOrder())
                .putShort(source)
                .array();
    }

}
