package eu.oberon.oss.tools.tsc.serz.values;


import eu.oberon.oss.tools.tsc.serz.DataType;

import java.nio.ByteBuffer;

/**
 * Represents a boolean value in the SERZ format.
 *
 * @param value the boolean value
 *
 * @author TigerLilly64
 * @since 1.0.0
 */
public record BoolValue(boolean value) implements DataValue<Boolean> {
    /**
     * {@inheritDoc}
     *
     * @since 1.0.0
     */
    @Override
    public DataType type() {
        return DataType.BOOL;
    }

    public BoolValue(byte[] bytes) {
        this(ByteBuffer.wrap(bytes).get() == 0x01);
    }

    @Override
    public byte[] toBytes(Boolean source) {
        return Boolean.TRUE.equals(source) ? new byte[]{0x01} : new byte[]{0x00};
    }
}
