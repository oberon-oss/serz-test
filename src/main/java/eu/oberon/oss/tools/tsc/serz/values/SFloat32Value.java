package eu.oberon.oss.tools.tsc.serz.values;


import eu.oberon.oss.tools.tsc.serz.DataType;
import eu.oberon.oss.tools.tsc.serz.util.BaseJaveTypesBufferReader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Represents a 32-bit floating-point value in the SERZ format.
 *
 * @param value the float value
 *
 * @author TigerLilly64
 * @since 1.0.0
 */
public record SFloat32Value(float value) implements DataValue<Float> {
    /**
     * {@inheritDoc}
     *
     * @since 1.0.0
     */
    @Override
    public DataType type() {
        return DataType.S_FLOAT32;
    }

    /**
     * Creates a 32-bit floating-point value from its binary representation.
     *
     * @param bytes the bytes to convert
     *
     * @since 1.0.0
     */
    public SFloat32Value(byte[] bytes) {
        this(BaseJaveTypesBufferReader.readFloat(bytes, ByteOrder.LITTLE_ENDIAN));
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0
     */
    @Override
    public byte[] toBytes(Float source) {
        return ByteBuffer.allocate(Float.BYTES)
                .order(ByteOrder.nativeOrder())
                .putFloat(source)
                .array();
    }

}
