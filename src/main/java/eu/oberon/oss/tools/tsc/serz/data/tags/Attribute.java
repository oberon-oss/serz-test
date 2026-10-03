package eu.oberon.oss.tools.tsc.serz.data.tags;

/**
 * Represents an attribute within a SERZ record.
 *
 * @author TigerLilly64
 * @since 1.0.0
 */
public interface Attribute {
    /**
     * Returns the byte offset of the attribute.
     *
     * @return the byte offset
     * @since 1.0.0
     */
    int offset();

    /**
     * Returns the name of the attribute.
     *
     * @return the attribute name
     * @since 1.0.0
     */
    String name();

    /**
     * Returns the value of the attribute.
     *
     * @param <T> the expected value type
     * @return the attribute value
     * @since 1.0.0
     */
    <T> T getValue();
}
