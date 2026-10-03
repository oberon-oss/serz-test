package eu.oberon.oss.tools.tsc.serz.data.tags;

/**
 * Common interface representing a SERZ tag record.
 *
 * @author TigerLilly64
 * @since 1.0.0
 */
public interface SERZTag {
    /**
     * Returns the type of the SERZ tag.
     *
     * @return the tag type
     * @since 1.0.0
     */
    SERZTagTypes getType();

    /**
     * Returns the byte offset of the tag in the binary data.
     *
     * @return the byte offset
     * @since 1.0.0
     */
    int getOffset();

    /**
     * Returns the length of the tag data payload in bytes.
     *
     * @return the payload length in bytes
     * @since 1.0.0
     */
    int getLength();
}
