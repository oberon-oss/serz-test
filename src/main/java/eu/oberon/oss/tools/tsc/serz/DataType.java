package eu.oberon.oss.tools.tsc.serz;

import eu.oberon.oss.tools.retriever.AbstractValueRetriever;
import eu.oberon.oss.tools.retriever.ValueRetriever;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

import static eu.oberon.oss.tools.retriever.AbstractValueRetriever.getRetriever;

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
    BOOL("bool", getRetriever("BOOLEAN")),
    /**
     * Unsigned 8-bit integer. Java type: {@code short}.
     *
     * @since 1.0.0
     */
    S_UINT8("sUInt8", getRetriever("UNSIGNED_BYTE")),
    /**
     * Signed 16-bit integer. Java type: {@code short}.
     *
     * @since 1.0.0
     */
    S_INT16("sInt16", getRetriever("SIGNED_SHORT")),
    /**
     * Signed 16-bit integer. Java type: {@code short}.
     *
     * @since 1.0.0
     */
    S_UINT16("sUInt16", getRetriever("UNSIGNED_SHORT")),
    /**
     * Signed 32-bit integer. Java type: {@code int}.
     *
     * @since 1.0.0
     */
    S_INT32("sInt32", getRetriever("SIGNED_INTEGER")),
    /**
     * Unsigned 32-bit integer. Java type: {@code long}.
     *
     * @since 1.0.0
     */
    S_UINT32("sUInt32", getRetriever("UNSIGNED_INTEGER")),
    /**
     * Unsigned 64-bit integer. Java type: {@code long}.
     *
     * @since 1.0.0
     */
    S_UINT64("sUInt64", getRetriever("UNSIGNED_LONG")),
    /**
     * 32-bit floating-point number. Java type: {@code float}.
     *
     * @since 1.0.0
     */
    S_FLOAT32("sFloat32", getRetriever("FLOAT")),
    /**
     * Delta-encoded string. Java type: {@link String}.
     *
     * @since 1.0.0
     */
    C_DELTA_STRING("cDeltaString", getRetriever("STRING"));

    private static final Map<String, DataType> BY_TEXT; /*= Map.of(
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
*/
    @Getter
    private final String serzDataTypeName;
    private final ValueRetriever retriever;

    DataType(String serzDataTypeName, ValueRetriever retriever) {
        this.serzDataTypeName = serzDataTypeName;
        this.retriever = retriever;
    }


    static {
        BY_TEXT = new HashMap<>();
        for (DataType dataType : values()) {
            BY_TEXT.put(dataType.getSerzDataTypeName(), dataType);
        }
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
    public static DataType bySerzDataTypeName(String text) {
        DataType dataType = BY_TEXT.get(text);
        if (dataType == null) {
            throw new IllegalArgumentException("Unknown data type: " + text);
        }
        return dataType;
    }

    /**
     * Returns the value retriever for the given data type.
     *
     * @param dataType the data type
     * @param <T>      the type of class that the value retriever will return.
     *
     * @return the value retriever for the given data type
     *
     * @since 1.0.0
     */
    public static <T extends AbstractValueRetriever> T retrieverByDataType(DataType dataType) {
        //noinspection unchecked
        return (T) dataType.retriever;
    }

    /**
     * Returns the value retriever for the given name.
     *
     * @param name the name of the data type
     * @param <T>  the type of class that the value retriever will return.
     *
     * @return the value retriever for the given data type
     *
     * @since 1.0.0
     */
    public static <T extends AbstractValueRetriever> T RetrieverBySerzDataTypeName(String name) {
        //noinspection unchecked
        return (T) bySerzDataTypeName(name).retriever;
    }
}
