package eu.oberon.oss.tools.tsc.serz.data.tags;

import eu.oberon.oss.tools.binaryreader.BinaryDataViewer;

public class SERZType52Tag extends AbstractSerzTag {
    public SERZType52Tag(BinaryDataViewer viewer, int offset, int dataSize) {
        super(SERZTagTypes.TYPE_52, viewer, offset, dataSize);
    }
}
