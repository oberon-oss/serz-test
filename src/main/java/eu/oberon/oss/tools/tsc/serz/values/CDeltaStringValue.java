package eu.oberon.oss.tools.tsc.serz.values;

import eu.oberon.oss.tools.tsc.serz.DataType;
import eu.oberon.oss.tools.tsc.serz.util.BaseJaveTypesBufferReader;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Represents a delta-encoded string value in the SERZ format.
 *
 * @param value the string value
 *
 * @author TigerLilly64
 * @since 1.0.0
 */
public record CDeltaStringValue(String value, Charset charset) implements DataValue<String> {

    /**
     * Constructs a {@code CDeltaStringValue} with the specified string value and assumes a default character set of UTF-8 for encoding.
     *
     * @param value the string value to encode
     *
     * @since 1.0.0
     */
    public CDeltaStringValue(String value) {
        this(value, StandardCharsets.UTF_8);
    }

    /**
     * Constructs a {@code CDeltaStringValue} from the specified byte array using the default character set of UTF-8.
     *
     * @param bytes the byte array representing the string value
     *
     * @since 1.0.0
     */
    public CDeltaStringValue(byte[] bytes) {
        this(BaseJaveTypesBufferReader.readString(bytes, StandardCharsets.UTF_8));
    }

    /**
     * Constructs a {@code CDeltaStringValue} from the specified byte array using the specified character set.
     *
     * @param bytes   the byte array representing the string value
     * @param charset the character set to use for decoding the byte array
     *
     * @since 1.0.0
     */
    public CDeltaStringValue(byte[] bytes, Charset charset) {
        this(BaseJaveTypesBufferReader.readString(bytes, charset), charset);
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0
     */
    @Override
    public DataType type() {
        return DataType.C_DELTA_STRING;
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0
     */
    @Override
    public byte[] toBytes(String source) {
        return source.getBytes(charset);
    }
}
