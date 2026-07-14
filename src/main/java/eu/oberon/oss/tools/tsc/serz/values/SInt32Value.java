package eu.oberon.oss.tools.tsc.serz.values;


import eu.oberon.oss.tools.tsc.serz.DataType;
import eu.oberon.oss.tools.tsc.serz.util.BaseJaveTypesBufferReader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Represents a signed 32-bit integer value in the SERZ format.
 *
 * @param value the integer value
 *
 * @author TigerLilly64
 * @since 1.0.0
 */
public record SInt32Value(int value) implements DataValue<Integer> {

    public SInt32Value(byte[] bytes) {
        this(BaseJaveTypesBufferReader.readInt(bytes, ByteOrder.LITTLE_ENDIAN));
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0
     */
    @Override
    public DataType type() {
        return DataType.S_INT32;
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0
     */
    @Override
    public byte[] toBytes(Integer source) {
        return ByteBuffer.allocate(Integer.BYTES)
                .order(ByteOrder.nativeOrder())
                .putInt(source)
                .array();
    }
}
