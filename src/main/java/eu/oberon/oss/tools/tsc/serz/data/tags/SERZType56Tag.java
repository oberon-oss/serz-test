package eu.oberon.oss.tools.tsc.serz.data.tags;

import eu.oberon.oss.tools.binaryreader.BinaryDataViewer;
import eu.oberon.oss.tools.tsc.serz.DataType;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class SERZType56Tag extends AbstractSerzTag {
    @Getter
    private int recordNameLength;
    @Getter
    private String recordName;

    public SERZType56Tag(BinaryDataViewer viewer, int offset, int dataSize) {
        super(SERZTagTypes.TYPE_56, viewer, offset, dataSize);
        dissect();
    }

    private void dissect() {
        int localOffset = dataOffset;

        recordNameLength = sInt32.getValue(viewer, localOffset);
        localOffset += 4;

        recordName = cDeltaString.getValue(viewer, localOffset, recordNameLength);

        FOUND_NAMES.put(localOffset, recordName);
        // There could be 0 or more additional attributes, as type 56 tags can contain attributes, contrary to type 50 tags,
        // which are container tags for type 56 tags.
        localOffset += recordNameLength;

        List<Type56Attribute> attributes = new ArrayList<>();

        while (Arrays.equals(viewer.peekBytes(localOffset, 2), new byte[]{(byte) 0xFF, (byte) 0xFF})) {
            localOffset += 2; // skip the FF FF bytes

            int attrNameLength = sInt32.getValue(viewer, localOffset);
            localOffset += 4; // Move past attribute name length

            String attrName = cDeltaString.getValue(viewer, localOffset, attrNameLength);
            localOffset += attrNameLength;

            switch (DataType.fromText(attrName)) {
                case BOOL -> {
                    attributes.add(new Type56Attribute(bool.getValue(viewer, localOffset), localOffset, attrName));
                    localOffset++;
                }
                case S_UINT32 -> {
                    attributes.add(new Type56Attribute(sInt32.getValue(viewer, localOffset), localOffset, attrName));
                    localOffset += 4;
                }
                case C_DELTA_STRING -> {
                    while (viewer.peekByte(localOffset) == (byte) 0xFF) {
                        localOffset++;
                    }

                    int stringLength = sInt32.getValue(viewer, localOffset);
                    localOffset += 4;
                    String value = cDeltaString.getValue(viewer, localOffset, stringLength);
                    attributes.add(new Type56Attribute(value, localOffset, attrName));
                    FOUND_NAMES.put(localOffset, value);
                    localOffset += attrNameLength;
                }
                case S_UINT8 -> {
                    attributes.add(new Type56Attribute(sUInt8.getValue(viewer, localOffset), localOffset, attrName));
                    localOffset++;
                }
                case S_FLOAT32 -> {
                    attributes.add(new Type56Attribute(sFloat32.getValue(viewer, localOffset), localOffset, attrName));
                    localOffset += 4;
                }
                default -> {
                    LOGGER.warn("Unknown attribute type: {}", attrName);
                }
            }
        }
        LOGGER.info("# of Attributes: {}", attributes.size());
        for (Type56Attribute attribute : attributes) {
            LOGGER.info("Attribute: {}, Offset: {}, Value: {}", attribute.name(), attribute.offset(), attribute.getValue());
        }
    }

    private record Type56Attribute(Object value, int offset, String name) implements Attribute {
        @Override
            public <T> T getValue() {
                return (T) value;
            }
        }
}
