package eu.oberon.oss.tools.tsc.serz.data.tags;

import eu.oberon.oss.tools.binaryreader.BinaryDataViewer;

/**
 * Represents a SERZ type 4E tag record.
 *
 * @author TigerLilly64
 * @since 1.0.0
 */
public class SERZType4ETag extends AbstractSerzTag {
    /**
     * Constructs a {@code SERZType4ETag} instance.
     *
     * @param viewer   the binary data viewer
     * @param offset   the byte offset of the tag in the binary data
     * @param dataSize the size in bytes of the tag payload
     * @since 1.0.0
     */
    public SERZType4ETag(BinaryDataViewer viewer, int offset, int dataSize) {
        super(SERZTagTypes.TYPE_4E, viewer, offset, dataSize);
    }
}
