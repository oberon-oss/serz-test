package eu.oberon.oss.tools.tsc.serz.parse;

import eu.oberon.oss.tools.tsc.serz.data.*;
import eu.oberon.oss.tools.tsc.serz.util.ByteDataBuffer;
import eu.oberon.oss.tools.tsc.serz.util.ByteDataBuffer.ByteBufferReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BinarySERZParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(BinarySERZParser.class);

    private final ByteDataBuffer buffer;

    public BinarySERZParser(ByteDataBuffer buffer) {
        this.buffer = buffer;
    }

    public void process() {
        ByteDataBuffer.ByteBufferReader reader = buffer.reader();

        List<SERZRecord> records = new ArrayList<>();

        reader.skip(8); // Do not process the SERZ header
        boolean inSyncMode = false;
        while (reader.hasRemaining()) {
            if (inSyncMode) {
                continue;
            } else {
                records.add(getRecord(reader));
            }
        }
    }

    @SuppressWarnings("java:S2629")
    private SERZRecord getRecord(ByteBufferReader reader) {
        SERZRecordTypes types = runMatch(reader);
        SERZRecord serzRecord = null;
        Objects.requireNonNull(types);
        switch (types) {
            case TYPE_70:
                int type70Offset = reader.offset();
                reader.skip(types.getBytes().length); // Skip the tag itself
                serzRecord = new SERZType70Record(type70Offset, reader.getBytes(2));
                break;
            case TYPE_4E:
                serzRecord = new SERZType4ERecord(reader.offset());
                reader.skip(types.getBytes().length); // This record is supposed to have NO data
                break;
            case TYPE_50, TYPE_56, TYPE_41:
                int recordOffset = reader.offset();
                reader.skip(types.getBytes().length); // Skip the tag itself

                switch (types) {
                    case SERZRecordTypes.TYPE_50 -> serzRecord = new SERZType50Record(recordOffset, reader.getBytes(getDataSizeForTag(reader)));
                    case SERZRecordTypes.TYPE_56 -> serzRecord = new SERZType56Record(recordOffset, reader.getBytes(getDataSizeForTag(reader)));
                    default -> serzRecord = new SERZType41Record(recordOffset, reader.getBytes(getDataSizeForTag(reader)));
                }

                break;
            case TYPE_XX:
                byte[] id = reader.getBytes(2);
                serzRecord = new SERZTypeXXRecord(id, reader.offset(), reader.getBytes(getDataSizeForTag(reader)));
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + types);
        }

        LOGGER.info(serzRecord.toString());

        return serzRecord;
    }

    private void createNamedRecordType() {

    }

    private int getDataSizeForTag(ByteBufferReader reader) {
        int dataSize = 0;

        while (dataSize < reader.remaining()) {
            if (isRecordStart(reader, dataSize)) {
                break;
            }

            dataSize++;
        }

        return dataSize;
    }

    private boolean isRecordStart(ByteBufferReader reader, int relativeOffset) {
        for (SERZRecordTypes type : SERZRecordTypes.values()) {
            byte[] recordTypeBytes = type.getBytes();

            if (recordTypeBytes.length == 0) {
                continue;
            }

            if (reader.remaining() - relativeOffset >= recordTypeBytes.length
                    && reader.matches(reader.offset() + relativeOffset, recordTypeBytes)) {
                return true;
            }
        }

        return false;
    }

    @SuppressWarnings("java:S2629")
    private SERZRecordTypes runMatch(ByteBufferReader reader) {
        for (SERZRecordTypes type : SERZRecordTypes.values()) {
            if (reader.remaining() >= 4 && reader.matches(type.getBytes())) {
                LOGGER.info("(Possible) Type {} serzRecord found @ {}", type, String.format("0x%08X", reader.offset()));
                return type;
            }
        }
        return null;
    }
}
