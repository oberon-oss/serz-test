package eu.oberon.oss.tools.tsc.serz.data.tags;

import eu.oberon.oss.tools.binaryreader.BinaryDataViewer;

/**
 * Represents a SERZ type 41 tag record.
 *
 * @author TigerLilly64
 * @since 1.0.0
 */
public class SERZType41Tag extends AbstractSerzTag {
    /**
     * Constructs a {@code SERZType41Tag} instance.
     *
     * @param viewer   the binary data viewer
     * @param offset   the byte offset of the tag in the binary data
     * @param dataSize the size in bytes of the tag payload
     * @since 1.0.0
     */
    public SERZType41Tag(BinaryDataViewer viewer, int offset, int dataSize) {
        super(SERZTagTypes.TYPE_41, viewer, offset, dataSize);
    }
}
