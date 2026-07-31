package eu.oberon.oss.tools.tsc.serz.parse;

import eu.oberon.oss.tools.binaryreader.BinaryDataReader;
import eu.oberon.oss.tools.binaryreader.BinaryDataReaderImpl;
import eu.oberon.oss.tools.binaryreader.BinaryDataViewer;
import eu.oberon.oss.tools.tsc.serz.data.tags.*;
import eu.oberon.oss.tools.tsc.serz.io.SERZBinaryFileTypeDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Parses a binary file format containing sequentially encoded SERZ records and processes them into appropriate SERZTag objects. The parser traverses the binary
 * data using a provided viewer and extracts record information based on predefined SERZ tag types.
 * <p>
 * This class is designed to handle binary data structures with a header and sequential records marked by unique identifiers defined in the SERZTagTypes enum.
 *
 * @author TigerLilly64
 * @since 1.0.0
 */
public class BinarySERZParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(BinarySERZParser.class);
    private static final int SERZ_HEADER_SIZE = 8;

    /**
     * Default constructor for BinarySERZParser.
     *
     * @since 1.0.0
     */
    public BinarySERZParser() {
    }

    /**
     * Processes the binary data provided by the viewer.
     *
     * @param viewer The BinaryDataViewer used to traverse and read the binary data.
     *
     * @throws NullPointerException     if the viewer is null
     * @throws IllegalArgumentException if the SERZ header is not found at the start of the data.
     * @since 1.0.0
     */
    public List<SERZTag> process(BinaryDataViewer viewer) {
        Objects.requireNonNull(viewer, "Parameter: viewer");

        BinaryDataReader reader = new BinaryDataReaderImpl(viewer);
        byte[] headerBytes = reader.readBytes(SERZ_HEADER_SIZE);
        if (SERZBinaryFileTypeDetector.probeContentType(headerBytes) == null) {
            throw new IllegalArgumentException("Expected SERZ header not found at start of data.");
        }

        List<SERZTag> records = new ArrayList<>();
        while (reader.hasRemaining()) {
            records.add(getRecord(reader));
        }

        Map<Integer, String> names = AbstractSerzTag.getNames();
        AtomicInteger count= new AtomicInteger(1);
        names.forEach((offset, name) -> {
            String output = String.format("idx: %4d, offset: %6x, Name: %s", count.getAndIncrement(), offset, name);
            LOGGER.info("{}", output);
        });
        return records;
    }

    @SuppressWarnings("java:S2629")
    private SERZTag getRecord(BinaryDataReader reader) {
        SERZTagTypes type = Objects.requireNonNull(
                runMatch(reader),
                () -> "Unable to match SERZ record at " + String.format("0x%08X", reader.offset())
        );
        BinaryDataViewer viewer = reader.getViewer();
        SERZTag serzTag;
        switch (type) {
            case TYPE_70:
                int type70Offset = reader.offset();
                reader.skip(type.getBytes().length); // Skip the tag itself
                serzTag = new SERZType70Tag(viewer, type70Offset, 2);
                reader.skip(2);
                break;
            case TYPE_4E:
                int type4EOffset = reader.offset();
                reader.skip(type.getBytes().length); // This tag is supposed to have NO data
                serzTag = new SERZType4ETag(viewer, type4EOffset, 0);
                break;
            case TYPE_50, TYPE_52, TYPE_56, TYPE_41:
                int recordOffset = reader.offset();
                reader.skip(type.getBytes().length); // Skip the tag itself

                int dataSize = getDataSizeForTag(reader);
                serzTag = switch (type) {
                    case TYPE_50 -> new SERZType50Tag(viewer, recordOffset, dataSize);
                    case TYPE_52 -> new SERZType52Tag(viewer, recordOffset, dataSize);
                    case TYPE_56 -> new SERZType56Tag(viewer, recordOffset, dataSize);
                    default -> new SERZType41Tag(viewer, recordOffset, dataSize);
                };
                reader.skip(dataSize);
                break;
            case TYPE_XX:
                int typeXXOffset = reader.offset();
                reader.skip(2); // Skip the unknown tag id bytes

                int unknownDataSize = getDataSizeForTag(reader);
                serzTag = new SERZTypeXXTag(viewer, typeXXOffset, unknownDataSize);
                reader.skip(unknownDataSize);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
        System.out.println("\n-----\n");
        return serzTag;
    }

    private int getDataSizeForTag(BinaryDataReader reader) {
        int dataSize = 0;

        while (dataSize < reader.remaining()) {
            if (isRecordStart(reader, dataSize)) {
                break;
            }

            dataSize++;
        }

        return dataSize;
    }

    private boolean isRecordStart(BinaryDataReader reader, int relativeOffset) {
        for (SERZTagTypes type : SERZTagTypes.values()) {
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
    private SERZTagTypes runMatch(BinaryDataReader reader) {
        for (SERZTagTypes type : SERZTagTypes.values()) {
            byte[] recordTypeBytes = type.getBytes();

            if (recordTypeBytes.length > 0 && reader.remaining() >= recordTypeBytes.length && reader.matches(reader.offset(), recordTypeBytes)) {
                LOGGER.debug("(Possible) Type {} SERZ record found @ {}", type, String.format("0x%08X", reader.offset()));
                return type;
            }
        }

        return SERZTagTypes.TYPE_XX;
    }
}