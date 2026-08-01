package eu.oberon.oss.tools.tsc.serz.data.tags;

import eu.oberon.oss.tools.binaryreader.BinaryDataViewer;
import eu.oberon.oss.tools.retriever.fixed.*;
import eu.oberon.oss.tools.retriever.varlen.text.StringValueRetriever;
import eu.oberon.oss.tools.tsc.serz.DataType;
import eu.oberon.oss.tools.tsc.serz.util.SimpleByteFormatter;

import java.util.TreeMap;

import static eu.oberon.oss.tools.tsc.serz.DataType.*;

public class AbstractSerzTag implements SERZTag {

    private final int offset;
    protected final int dataOffset;
    protected final int dataSize;
    protected final SERZTagTypes type;
    protected final BinaryDataViewer viewer;
    protected static final TreeMap<Integer, String> FOUND_NAMES = new TreeMap<>();

    protected AbstractSerzTag(SERZTagTypes type, BinaryDataViewer viewer, int offset, int dataSize) {
        this.type = type;
        this.viewer = viewer;
        this.offset = offset;
        this.dataOffset = offset + type.headerIdLength();
        this.dataSize = dataSize;
    }

    public static TreeMap<Integer, String> getNames() {
        return FOUND_NAMES;
    }

    @Override
    public SERZTagTypes getType() {
        return type;
    }

    @Override
    public int getOffset() {
        return offset;
    }

    @Override
    public int getLength() {
        return dataSize;
    }

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

    protected BinaryDataViewer getViewer() {
        return viewer;
    }

    protected String formatPayLoadBytes() {
        return SimpleByteFormatter.formatBytes(viewer.peekBytes(dataOffset, dataSize));
    }

    protected static final SignedIntegerRetriever sInt32 = DataType.retrieverByDataType(S_INT32);
    protected static final UnsignedIntegerRetriever sUInt32 = DataType.retrieverByDataType(S_UINT32);
    protected static final StringValueRetriever cDeltaString = DataType.retrieverByDataType(C_DELTA_STRING);
    protected static final BooleanRetriever bool = DataType.retrieverByDataType(BOOL);
    protected static final UnsignedByteRetriever sUInt8 = DataType.retrieverByDataType(S_UINT8);
    protected static final UnsignedShortRetriever sUInt16 = DataType.retrieverByDataType(S_UINT16);
    protected static final SignedShortRetriever sInt16 = DataType.retrieverByDataType(S_INT16);
    protected static final FloatRetriever sFloat32 = DataType.retrieverByDataType(S_FLOAT32);
}
