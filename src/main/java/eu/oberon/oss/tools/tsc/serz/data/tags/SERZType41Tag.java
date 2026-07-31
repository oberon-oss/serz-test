package eu.oberon.oss.tools.tsc.serz.data.tags;

import eu.oberon.oss.tools.binaryreader.BinaryDataViewer;

public class SERZType41Tag extends AbstractSerzTag {
    public SERZType41Tag(BinaryDataViewer viewer, int offset, int dataSize) {
        super(SERZTagTypes.TYPE_41, viewer, offset, dataSize);
    }
}
