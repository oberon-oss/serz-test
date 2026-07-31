package eu.oberon.oss.tools.tsc.serz.data.tags;

import eu.oberon.oss.tools.binaryreader.BinaryDataViewer;

public class SERZType4ETag extends AbstractSerzTag {
    public SERZType4ETag(BinaryDataViewer viewer, int offset, int dataSize) {
        super(SERZTagTypes.TYPE_4E, viewer, offset, dataSize);
    }
}
