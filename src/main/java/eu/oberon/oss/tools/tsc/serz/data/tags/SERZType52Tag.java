package eu.oberon.oss.tools.tsc.serz.data.tags;

import eu.oberon.oss.tools.binaryreader.BinaryDataViewer;

/**
 * Represents a SERZ type 52 tag record.
 *
 * @author TigerLilly64
 * @since 1.0.0
 */
public class SERZType52Tag extends AbstractSerzTag {
    /**
     * Constructs a {@code SERZType52Tag} instance.
     *
     * @param viewer   the binary data viewer
     * @param offset   the byte offset of the tag in the binary data
     * @param dataSize the size in bytes of the tag payload
     * @since 1.0.0
     */
    public SERZType52Tag(BinaryDataViewer viewer, int offset, int dataSize) {
        super(SERZTagTypes.TYPE_52, viewer, offset, dataSize);
    }
}
