package eu.oberon.oss.tools.tsc.serz.data.tags;

import eu.oberon.oss.tools.binaryreader.BinaryDataViewer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SERZType70Tag extends AbstractSerzTag {
    private static final Logger LOGGER = LoggerFactory.getLogger(SERZType70Tag.class);

    private int unsignedValue;
    private int signedValue;

    public SERZType70Tag(BinaryDataViewer viewer, int offset, int dataSize) {
        super(SERZTagTypes.TYPE_70, viewer, offset, dataSize);
        dissect();
    }

    private void dissect() {
        unsignedValue = sUInt16.getValue(viewer, dataOffset);
        signedValue = sInt16.getValue(viewer, dataOffset);
        if (signedValue == unsignedValue) {
//            LOGGER.info("Data: {}",signedValue);
        }
        else {
//            LOGGER.debug("Unsigned value: {}, signed value: {}", unsignedValue, signedValue);
        }
    }
}
