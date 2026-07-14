package eu.oberon.oss.tools.tsc.serz.data;

import eu.oberon.oss.tools.tsc.serz.util.ByteDataBuffer;
import eu.oberon.oss.tools.tsc.serz.util.SimpleByteFormatter;

import java.util.Arrays;

public class AbstractSerzRecord implements SERZRecord {
    private final SERZRecordTypes type;
    private final byte[] payLoad;

    private final int offset;

    protected AbstractSerzRecord(SERZRecordTypes type, int offset, byte[] payLoad) {
        this.type = type;
        this.offset = offset;
        this.payLoad = Arrays.copyOf(payLoad, payLoad.length);
    }

    @Override
    public SERZRecordTypes getType() {
        return type;
    }

    @Override
    public int getOffset() {
        return offset;
    }

    @Override
    public int getLength() {
        return payLoad.length;
    }

    protected byte[] getPayLoad() {
        return payLoad;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("Record type: ").append(getType()).append("\nData (").append(getLength()).append(") :\n");
        sb.append(formatPayLoadBytes());

        return sb.toString();
    }

    protected String formatPayLoadBytes(){
        return SimpleByteFormatter.formatBytes(payLoad);
    }


}
