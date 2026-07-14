package eu.oberon.oss.tools.tsc.serz.values;


import eu.oberon.oss.tools.tsc.serz.DataType;

/**
 * Represents a data value in the SERZ format.
 *
 * @author TigerLilly64
 * @since 1.0.0
 */
public sealed interface DataValue<T> permits BoolValue, CDeltaStringValue, SFloat32Value, SInt16Value, SUInt16Value, SInt32Value, SUInt32Value,
        SUInt64Value,
        SUInt8Value {
    /**
     * Returns the data type of this value.
     *
     * @return the data type
     *
     * @since 1.0.0
     */
    DataType type();

    byte[] toBytes(T source);
}
