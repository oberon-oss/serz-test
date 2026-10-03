package eu.oberon.oss.tools.tsc.serz.data.tags;

import eu.oberon.oss.tools.binaryreader.BinaryDataViewer;

/**
 * Represents an unknown or unclassified SERZ tag record.
 *
 * @author TigerLilly64
 * @since 1.0.0
 */
public class SERZTypeXXTag extends AbstractSerzTag {

    /**
     * Constructs a {@code SERZTypeXXTag} instance.
     *
     * @param viewer   the binary data viewer
     * @param offset   the byte offset of the tag in the binary data
     * @param dataSize the size in bytes of the tag payload
     *
     * @since 1.0.0
     */
    public SERZTypeXXTag(BinaryDataViewer viewer, int offset, int dataSize) {
        // We call the super constructor with the dataSize +2, as the tag bytes are not included in the dataSize, but the offset is pointing to the tag bytes.
        // Otherwise, we lose 2 bytes in this type of records.
        super(SERZTagTypes.TYPE_XX, viewer, offset, dataSize + 2);
    }

}
