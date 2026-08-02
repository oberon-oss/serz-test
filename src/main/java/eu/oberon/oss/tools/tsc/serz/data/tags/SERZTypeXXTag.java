package eu.oberon.oss.tools.tsc.serz.data.tags;

import eu.oberon.oss.tools.binaryreader.BinaryDataViewer;
import eu.oberon.oss.tools.tsc.serz.util.SimpleByteFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SERZTypeXXTag extends AbstractSerzTag {
    private static final Logger LOGGER = LoggerFactory.getLogger(SERZTypeXXTag.class);

    public SERZTypeXXTag(BinaryDataViewer viewer, int offset, int dataSize) {
        // We call the super constructor with the dataSize +2, as the tag bytes are not included in the dataSize, but the offset is pointing to the tag bytes.
        // Otherwise, we lose 2 bytes in this type of records.
        super(SERZTagTypes.TYPE_XX, viewer, offset, dataSize + 2);
        dissect();
    }

    private void dissect() {
        // String info = String.format("ID = 0x%X%X", viewer.peekByte(getOffset()), viewer.peekByte(getOffset() + 1));
    }
}
