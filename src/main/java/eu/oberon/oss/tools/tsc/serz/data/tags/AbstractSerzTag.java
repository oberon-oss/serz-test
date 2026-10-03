package eu.oberon.oss.tools.tsc.serz.data.tags;

import eu.oberon.oss.tools.binaryreader.BinaryDataViewer;
import eu.oberon.oss.tools.retriever.fixed.*;
import eu.oberon.oss.tools.retriever.varlen.text.StringValueRetriever;
import eu.oberon.oss.tools.tsc.serz.DataType;
import eu.oberon.oss.tools.tsc.serz.util.SimpleByteFormatter;

import java.util.SortedMap;
import java.util.TreeMap;

import static eu.oberon.oss.tools.tsc.serz.DataType.*;

/**
 * Abstract base class for all SERZ tags.
 *
 * @author TigerLilly64
 * @since 1.0.0
 */
public class AbstractSerzTag implements SERZTag {

    private final int offset;
    /**
     * Offset in bytes where the payload data begins.
     *
     * @since 1.0.0
     */
    protected final int dataOffset;
    /**
     * Size in bytes of the tag payload.
     *
     * @since 1.0.0
     */
    protected final int dataSize;
    /**
     * The type of the SERZ tag.
     *
     * @since 1.0.0
     */
    protected final SERZTagTypes type;
    /**
     * The binary data viewer used to read tag content.
     *
     * @since 1.0.0
     */
    protected final BinaryDataViewer viewer;
    /**
     * Registry of names discovered during parsing mapped by offset.
     *
     * @since 1.0.0
     */
    protected static final TreeMap<Integer, String> FOUND_NAMES = new TreeMap<>();

    /**
     * Constructs an {@code AbstractSerzTag} with the specified type, viewer, offset, and data size.
     *
     * @param type     the SERZ tag type
     * @param viewer   the binary data viewer
     * @param offset   the byte offset of the tag in the binary data
     * @param dataSize the size in bytes of the tag payload
     * @since 1.0.0
     */
    protected AbstractSerzTag(SERZTagTypes type, BinaryDataViewer viewer, int offset, int dataSize) {
        this.type = type;
        this.viewer = viewer;
        this.offset = offset;
        this.dataOffset = offset + type.headerIdLength();
        this.dataSize = dataSize;
    }

    /**
     * Returns the map of discovered names and their offsets.
     *
     * @return tree map of names discovered during parsing
     * @since 1.0.0
     */
    public static SortedMap<Integer, String> getNames() {
        return FOUND_NAMES;
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0
     */
    @Override
    public SERZTagTypes getType() {
        return type;
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0
     */
    @Override
    public int getOffset() {
        return offset;
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0
     */
    @Override
    public int getLength() {
        return dataSize;
    }

    /**
     * Returns a string representation of the SERZ tag including offset, type, and formatted payload bytes.
     *
     * @return formatted string representation of this tag
     * @since 1.0.0
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb
                .append("Tag offset=").append(String.format("0x%08X", offset)).append(" (").append(offset).append("), ")
                .append("Record type: ").append(getType())
                .append(" (tag ID size=").append(type.headerIdLength()).append("), ")
                .append("Data offset=").append(String.format("0x%08X", dataOffset)).append(" (").append(dataOffset).append("), ")
                .append("Data size=").append(getLength()).append(" :\n");
        sb.append(formatPayLoadBytes());

        return sb.toString();
    }

    /**
     * Returns the binary data viewer for this tag.
     *
     * @return the binary data viewer
     * @since 1.0.0
     */
    protected BinaryDataViewer getViewer() {
        return viewer;
    }

    /**
     * Formats the payload bytes into a human-readable string representation.
     *
     * @return the formatted payload bytes string
     * @since 1.0.0
     */
    protected String formatPayLoadBytes() {
        return SimpleByteFormatter.formatBytes(viewer.peekBytes(dataOffset, dataSize));
    }

    /**
     * Value retriever for signed 32-bit integer values.
     *
     * @since 1.0.0
     */
    protected static final SignedIntegerRetriever sInt32 = DataType.retrieverByDataType(S_INT32);
    /**
     * Value retriever for unsigned 32-bit integer values.
     *
     * @since 1.0.0
     */
    protected static final UnsignedIntegerRetriever sUInt32 = DataType.retrieverByDataType(S_UINT32);
    /**
     * Value retriever for delta-encoded string values.
     *
     * @since 1.0.0
     */
    protected static final StringValueRetriever cDeltaString = DataType.retrieverByDataType(C_DELTA_STRING);
    /**
     * Value retriever for boolean values.
     *
     * @since 1.0.0
     */
    protected static final BooleanRetriever bool = DataType.retrieverByDataType(BOOL);
    /**
     * Value retriever for unsigned 8-bit integer values.
     *
     * @since 1.0.0
     */
    protected static final UnsignedByteRetriever sUInt8 = DataType.retrieverByDataType(S_UINT8);
    /**
     * Value retriever for unsigned 16-bit integer values.
     *
     * @since 1.0.0
     */
    protected static final UnsignedShortRetriever sUInt16 = DataType.retrieverByDataType(S_UINT16);
    /**
     * Value retriever for signed 16-bit integer values.
     *
     * @since 1.0.0
     */
    protected static final SignedShortRetriever sInt16 = DataType.retrieverByDataType(S_INT16);
    /**
     * Value retriever for 32-bit floating-point values.
     *
     * @since 1.0.0
     */
    protected static final FloatRetriever sFloat32 = DataType.retrieverByDataType(S_FLOAT32);
}
