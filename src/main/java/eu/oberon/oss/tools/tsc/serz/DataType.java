package eu.oberon.oss.tools.tsc.serz;

import java.util.Map;

/**
 * Enumeration of supported data types in the SERZ format. Each entry corresponds to a specific representation used during serialization and deserialization.
 *
 * @author TigerLilly64
 * @since 1.0.0
 */
public enum DataType {
    /**
     * Boolean data type. Java type: {@code boolean}.
     *
     * @since 1.0.0
     */
    BOOL("bool"),
    /**
     * Unsigned 8-bit integer. Java type: {@code short}.
     *
     * @since 1.0.0
     */
    S_UINT8("sUInt8"),
    /**
     * Signed 16-bit integer. Java type: {@code short}.
     *
     * @since 1.0.0
     */
    S_INT16("sInt16"),
    /**
     * Signed 16-bit integer. Java type: {@code short}.
     *
     * @since 1.0.0
     */
    S_UINT16("sUInt16"),
    /**
     * Signed 32-bit integer. Java type: {@code int}.
     *
     * @since 1.0.0
     */
    S_INT32("sInt32"),
    /**
     * Unsigned 32-bit integer. Java type: {@code long}.
     *
     * @since 1.0.0
     */
    S_UINT32("sUInt32"),
    /**
     * Unsigned 64-bit integer. Java type: {@code long}.
     *
     * @since 1.0.0
     */
    S_UINT64("sUInt64"),
    /**
     * 32-bit floating-point number. Java type: {@code float}.
     *
     * @since 1.0.0
     */
    S_FLOAT32("sFloat32"),
    /**
     * Delta-encoded string. Java type: {@link String}.
     *
     * @since 1.0.0
     */
    C_DELTA_STRING("cDeltaString");

    private static final Map<String, DataType> BY_TEXT = Map.of(
            "bool", BOOL,
            "sUInt8", S_UINT8,
            "sInt16", S_INT16,
            "sUInt16", S_UINT16,
            "sInt32", S_INT32,
            "sUInt32", S_UINT32,
            "sUInt64", S_UINT64,
            "sFloat32", S_FLOAT32,
            "cDeltaString", C_DELTA_STRING
    );

    private final String text;

    DataType(String text) {
        this.text = text;
    }

    /**
     * Returns the text representation of this data type.
     *
     * @return the text representation of this data type
     *
     * @since 1.0.0
     */
    public String text() {
        return text;
    }

    /**
     * Returns the data type corresponding to the given text representation.
     *
     * @param text the text representation of the data type
     *
     * @return the data type corresponding to the given text representation
     *
     * @throws IllegalArgumentException if the given text representation is unknown
     * @since 1.0.0
     */
    public static DataType fromText(String text) {
        DataType dataType = BY_TEXT.get(text);
        if (dataType == null) {
            throw new IllegalArgumentException("Unknown data type: " + text);
        }
        return dataType;
    }
}
