package eu.oberon.oss.tools.tsc.serz.data.tags;

import java.util.Arrays;

/**
 * Enumerates the currently known SERZ record types.
 *
 * @author TigerLilly64
 * @since 1.0.0
 */
public enum SERZTagTypes {

    TYPE_41(new byte[]{(byte) 0xFF, 0x41, (byte) 0xFF, (byte) 0xFF}),
    TYPE_42(new byte[]{(byte) 0xFF, 0x41, (byte) 0xFF, (byte) 0xFF}),
    /**
     * At present, the type 4E record seems to be a record denoting a {@code <nul/>} entity.
     *
     * @since 1.0.0
     */
    TYPE_4E(new byte[]{(byte) 0xFF, 0x4E}),
    /**
     * At present, the type 50 record seems to be a container that may contain 1 or more child records.
     *
     * @since 1.0.0
     */
    TYPE_50(new byte[]{(byte) 0xFF, 0x50, (byte) 0xFF, (byte) 0xFF}),
    TYPE_52(new byte[]{(byte) 0xFF, 0x52, (byte) 0xFF, (byte) 0xFF}),
    TYPE_56(new byte[]{(byte) 0xFF, 0x56, (byte) 0xFF, (byte) 0xFF}),
    TYPE_70(new byte[]{(byte) 0xFF, 0x70}),
    /**
     * A special type that represents unknown bytes between tags or tags not yet recognized.
     *
     * @since 1.0.0
     */
    TYPE_XX(new byte[0]);

    private final byte[] bytes;

    SERZTagTypes(byte[] bytes) {
        this.bytes = bytes;
    }

    public int headerIdLength(){
        return bytes.length;
    }

    public byte[] getBytes() {
        return Arrays.copyOf(bytes, bytes.length);
    }
}
