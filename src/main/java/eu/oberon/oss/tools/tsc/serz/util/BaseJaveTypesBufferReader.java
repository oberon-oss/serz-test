package eu.oberon.oss.tools.tsc.serz.util;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.Objects;
import java.util.function.IntUnaryOperator;

/**
 * Allocation-free primitive reader wrapper around {@link ByteDataBuffer.ByteBufferReader}.
 * <p>
 * This class reads non-byte primitive values directly from the wrapped reader's byte data using {@code peekByte(...)} and advances the wrapped reader using
 * {@code skip(...)} for get-style methods. It avoids creating temporary {@link ByteBuffer} instances for primitive reads.
 *
 * @author TigerLilly64
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public record BaseJaveTypesBufferReader(ByteDataBuffer.ByteBufferReader reader) {
    private static final String FIELD_NAME_BYTES = "bytes";
    private static final String FIELD_NAME_BYTE_ORDER = "byteOrder";
    private static final String FIELD_NAME_CHARSET = "charset";

    /**
     * Creates a new primitive reader around the given byte buffer reader.
     *
     * @param reader the reader to wrap
     *
     * @throws NullPointerException if the reader parameter is null
     * @since 1.0.0
     */
    public BaseJaveTypesBufferReader(ByteDataBuffer.ByteBufferReader reader) {
        this.reader = Objects.requireNonNull(reader, "reader");
    }

    /**
     * Reads the next signed 16-bit integer and advances the wrapped reader by two bytes.
     *
     * @return the signed 16-bit integer
     *
     * @since 1.0.0
     */
    public short getShort() {
        short result = peekShort();
        reader.skip(Short.BYTES);
        return result;
    }

    /**
     * Reads the next unsigned 16-bit integer and advances the wrapped reader by two bytes.
     *
     * @return the unsigned 16-bit integer as an int
     *
     * @since 1.0.0
     */
    public int getUnsignedShort() {
        int result = peekUnsignedShort();
        reader.skip(Short.BYTES);
        return result;
    }

    /**
     * Reads the next signed 32-bit integer and advances the wrapped reader by four bytes.
     *
     * @return the signed 32-bit integer
     *
     * @since 1.0.0
     */
    public int getInt() {
        int result = peekInt();
        reader.skip(Integer.BYTES);
        return result;
    }

    /**
     * Reads the next unsigned 32-bit integer and advances the wrapped reader by four bytes.
     *
     * @return the unsigned 32-bit integer as a long value
     *
     * @since 1.0.0
     */
    public long getUnsignedInt() {
        long result = peekUnsignedInt();
        reader.skip(Integer.BYTES);
        return result;
    }

    /**
     * Reads the next signed 64-bit integer and advances the wrapped reader by eight bytes.
     *
     * @return the signed 64-bit integer
     *
     * @since 1.0.0
     */
    public long getLong() {
        long result = peekLong();
        reader.skip(Long.BYTES);
        return result;
    }

    /**
     * Reads the next unsigned 64-bit integer and advances the wrapped reader by eight bytes.
     *
     * @return the unsigned 64-bit integer as a {@link BigInteger}
     *
     * @since 1.0.0
     */
    public BigInteger getUnsignedLong() {
        BigInteger result = peekUnsignedLong();
        reader.skip(Long.BYTES);
        return result;
    }

    /**
     * Reads the next 32-bit floating-point value and advances the wrapped reader by four bytes.
     *
     * @return the float value
     *
     * @since 1.0.0
     */
    public float getFloat() {
        float result = peekFloat();
        reader.skip(Float.BYTES);
        return result;
    }

    /**
     * Reads the next 64-bit floating-point value and advances the wrapped reader by eight bytes.
     *
     * @return the double value
     *
     * @since 1.0.0
     */
    public double getDouble() {
        double result = peekDouble();
        reader.skip(Double.BYTES);
        return result;
    }

    /**
     * Reads the next {@code length} bytes as a string decoded with the platform default charset and advances the wrapped reader by {@code length} bytes.
     *
     * @param length the number of bytes to read
     *
     * @return the decoded string
     *
     * @since 1.0.0
     */
    public String getString(int length) {
        String result = peekString(length);
        reader.skip(length);
        return result;
    }

    /**
     * Reads the next {@code length} bytes as a string decoded with the given charset and advances the wrapped reader by {@code length} bytes.
     *
     * @param length  the number of bytes to read
     * @param charset the charset to use when decoding the string
     *
     * @return the decoded string
     *
     * @since 1.0.0
     */
    public String getString(int length, Charset charset) {
        String result = peekString(length, charset);
        reader.skip(length);
        return result;
    }

    /**
     * Peeks a signed 16-bit integer at the current offset.
     *
     * @return the signed 16-bit integer
     *
     * @since 1.0.0
     */
    public short peekShort() {
        return peekShort(0);
    }

    /**
     * Peeks an unsigned 16-bit integer at the current offset.
     *
     * @return the unsigned 16-bit integer as an int
     *
     * @since 1.0.0
     */
    public int peekUnsignedShort() {
        return peekUnsignedShort(0);
    }

    /**
     * Peeks a signed 32-bit integer at the current offset.
     *
     * @return the signed 32-bit integer
     *
     * @since 1.0.0
     */
    public int peekInt() {
        return peekInt(0);
    }

    /**
     * Peeks an unsigned 32-bit integer at the current offset.
     *
     * @return the unsigned 32-bit integer as a long value
     *
     * @since 1.0.0
     */
    public long peekUnsignedInt() {
        return peekUnsignedInt(0);
    }

    /**
     * Peeks a signed 64-bit integer at the current offset.
     *
     * @return the signed 64-bit integer
     *
     * @since 1.0.0
     */
    public long peekLong() {
        return peekLong(0);
    }

    /**
     * Peeks an unsigned 64-bit integer at the given relative offset.
     *
     * @param relativeOffset the offset relative to the wrapped reader's current offset
     *
     * @return the unsigned 64-bit integer as a {@link BigInteger}
     *
     * @since 1.0.0
     */
    public BigInteger peekUnsignedLong(int relativeOffset) {
        ensureAvailable(relativeOffset, Long.BYTES);
        return readUnsignedLong(index -> unsignedByte(reader, index), relativeOffset, reader.byteOrder());
    }

    /**
     * Peeks an unsigned 64-bit integer at the current offset.
     *
     * @return the unsigned 64-bit integer as a {@link BigInteger}
     *
     * @since 1.0.0
     */
    public BigInteger peekUnsignedLong() {
        return peekUnsignedLong(0);
    }


    /**
     * Peeks a 32-bit floating-point value at the current offset.
     *
     * @return the float value
     *
     * @since 1.0.0
     */
    public float peekFloat() {
        return peekFloat(0);
    }

    /**
     * Peeks a 64-bit floating-point value at the current offset.
     *
     * @return the double value
     *
     * @since 1.0.0
     */
    public double peekDouble() {
        return peekDouble(0);
    }

    /**
     * Peeks the next {@code length} bytes as a string decoded with the platform default charset without advancing the wrapped reader.
     *
     * @param length the number of bytes to read
     *
     * @return the decoded string
     *
     * @since 1.0.0
     */
    public String peekString(int length) {
        return peekString(0, length);
    }

    /**
     * Peeks the next {@code length} bytes as a string decoded with the given charset without advancing the wrapped reader.
     *
     * @param length  the number of bytes to read
     * @param charset the charset to use when decoding the string
     *
     * @return the decoded string
     *
     * @since 1.0.0
     */
    public String peekString(int length, Charset charset) {
        return peekString(0, length, charset);
    }

    /**
     * Peeks {@code length} bytes at the given relative offset as a string decoded with the platform default charset without advancing the wrapped reader.
     *
     * @param relativeOffset the offset relative to the wrapped reader's current offset
     * @param length         the number of bytes to read
     *
     * @return the decoded string
     *
     * @since 1.0.0
     */
    public String peekString(int relativeOffset, int length) {
        return peekString(relativeOffset, length, Charset.defaultCharset());
    }

    /**
     * Peeks {@code length} bytes at the given relative offset as a string decoded with the given charset without advancing the wrapped reader.
     *
     * @param relativeOffset the offset relative to the wrapped reader's current offset
     * @param length         the number of bytes to read
     * @param charset        the charset to use when decoding the string
     *
     * @return the decoded string
     *
     * @since 1.0.0
     */
    public String peekString(int relativeOffset, int length, Charset charset) {
        ensureAvailable(relativeOffset, length);
        Objects.requireNonNull(charset, FIELD_NAME_CHARSET);

        return reader.peekString(relativeOffset, length, charset);
    }

    /**
     * Peeks a signed 16-bit integer at the given relative offset.
     *
     * @param relativeOffset the offset relative to the wrapped reader's current offset
     *
     * @return the signed 16-bit integer
     *
     * @since 1.0.0
     */
    public short peekShort(int relativeOffset) {
        ensureAvailable(relativeOffset, Short.BYTES);
        return readShort(reader, relativeOffset);
    }

    /**
     * Peeks an unsigned 16-bit integer at the given relative offset.
     *
     * @param relativeOffset the offset relative to the wrapped reader's current offset
     *
     * @return the unsigned 16-bit integer as an int
     *
     * @since 1.0.0
     */
    public int peekUnsignedShort(int relativeOffset) {
        return Short.toUnsignedInt(peekShort(relativeOffset));
    }

    /**
     * Peeks a signed 32-bit integer at the given relative offset.
     *
     * @param relativeOffset the offset relative to the wrapped reader's current offset
     *
     * @return the signed 32-bit integer
     *
     * @since 1.0.0
     */
    public int peekInt(int relativeOffset) {
        ensureAvailable(relativeOffset, Integer.BYTES);
        return readInt(reader, relativeOffset);
    }

    /**
     * Peeks an unsigned 32-bit integer at the given relative offset.
     *
     * @param relativeOffset the offset relative to the wrapped reader's current offset
     *
     * @return the unsigned 32-bit integer as a long value
     *
     * @since 1.0.0
     */
    public long peekUnsignedInt(int relativeOffset) {
        return Integer.toUnsignedLong(peekInt(relativeOffset));
    }

    /**
     * Peeks a signed 64-bit integer at the given relative offset.
     *
     * @param relativeOffset the offset relative to the wrapped reader's current offset
     *
     * @return the signed 64-bit integer
     *
     * @since 1.0.0
     */
    public long peekLong(int relativeOffset) {
        ensureAvailable(relativeOffset, Long.BYTES);
        return readLong(index -> unsignedByte(reader, index), relativeOffset, reader.byteOrder());
    }

    /**
     * Peeks a 32-bit floating-point value at the given relative offset.
     *
     * @param relativeOffset the offset relative to the wrapped reader's current offset
     *
     * @return the float value
     *
     * @since 1.0.0
     */
    public float peekFloat(int relativeOffset) {
        ensureAvailable(relativeOffset, Float.BYTES);
        return readFloat(reader, relativeOffset);
    }

    /**
     * Peeks a 64-bit floating-point value at the given relative offset.
     *
     * @param relativeOffset the offset relative to the wrapped reader's current offset
     *
     * @return the double value
     *
     * @since 1.0.0
     */
    public double peekDouble(int relativeOffset) {
        ensureAvailable(relativeOffset, Double.BYTES);
        return readDouble(reader, relativeOffset);
    }

    /**
     * Reads a signed 16-bit integer at the wrapped reader's current offset without advancing it.
     *
     * @return the signed 16-bit integer
     *
     * @since 1.0.0
     */
    public short readShort() {
        return readShort(reader, 0);
    }

    /**
     * Reads a signed 16-bit integer from the start of the given byte array using the platform native byte order.
     *
     * @param bytes the bytes to read from
     *
     * @return the signed 16-bit integer
     *
     * @throws NullPointerException      if bytes is null
     * @throws IndexOutOfBoundsException if there are fewer than two bytes available
     * @since 1.0.0
     */
    public static short readShort(byte[] bytes) {
        return readShort(bytes, ByteOrder.nativeOrder());
    }

    /**
     * Reads a signed 16-bit integer from the start of the given byte array.
     *
     * @param bytes     the bytes to read from
     * @param byteOrder the byte order to use
     *
     * @return the signed 16-bit integer
     *
     * @throws NullPointerException      if bytes or byteOrder is null
     * @throws IndexOutOfBoundsException if there are fewer than two bytes available
     * @since 1.0.0
     */
    public static short readShort(byte[] bytes, ByteOrder byteOrder) {
        return readShort(bytes, 0, byteOrder);
    }

    /**
     * Reads a signed 16-bit integer from the given absolute offset in the byte array.
     *
     * @param bytes     the bytes to read from
     * @param offset    the absolute offset to read from
     * @param byteOrder the byte order to use
     *
     * @return the signed 16-bit integer
     *
     * @throws NullPointerException      if bytes or byteOrder is null
     * @throws IndexOutOfBoundsException if there are fewer than two bytes available at the given offset
     * @since 1.0.0
     */
    public static short readShort(byte[] bytes, int offset, ByteOrder byteOrder) {
        ensureAvailable(bytes, offset, Short.BYTES);
        Objects.requireNonNull(byteOrder, FIELD_NAME_BYTE_ORDER);

        if (byteOrder == ByteOrder.BIG_ENDIAN) {
            return (short) ((unsignedByte(bytes, offset) << 8)
                    | unsignedByte(bytes, offset + 1));
        }

        return (short) (unsignedByte(bytes, offset)
                | (unsignedByte(bytes, offset + 1) << 8));
    }

    /**
     * Reads an unsigned 16-bit integer from the start of the given byte array using the platform native byte order.
     *
     * @param bytes the bytes to read from
     *
     * @return the unsigned 16-bit integer as an int
     *
     * @since 1.0.0
     */
    public static int readUnsignedShort(byte[] bytes) {
        return readUnsignedShort(bytes, ByteOrder.nativeOrder());
    }

    /**
     * Reads an unsigned 16-bit integer from the start of the given byte array.
     *
     * @param bytes     the bytes to read from
     * @param byteOrder the byte order to use
     *
     * @return the unsigned 16-bit integer as an int
     *
     * @since 1.0.0
     */
    public static int readUnsignedShort(byte[] bytes, ByteOrder byteOrder) {
        return Short.toUnsignedInt(readShort(bytes, byteOrder));
    }

    /**
     * Reads an unsigned 16-bit integer from the given absolute offset in the byte array.
     *
     * @param bytes     the bytes to read from
     * @param offset    the absolute offset to read from
     * @param byteOrder the byte order to use
     *
     * @return the unsigned 16-bit integer as an int
     *
     * @since 1.0.0
     */
    public static int readUnsignedShort(byte[] bytes, int offset, ByteOrder byteOrder) {
        return Short.toUnsignedInt(readShort(bytes, offset, byteOrder));
    }

    /**
     * Reads a signed 32-bit integer from the start of the given byte array using the platform native byte order.
     *
     * @param bytes the bytes to read from
     *
     * @return the signed 32-bit integer
     *
     * @throws NullPointerException      if bytes is null
     * @throws IndexOutOfBoundsException if there are fewer than four bytes available
     * @since 1.0.0
     */
    public static int readInt(byte[] bytes) {
        return readInt(bytes, ByteOrder.nativeOrder());
    }

    /**
     * Reads a signed 32-bit integer from the start of the given byte array.
     *
     * @param bytes     the bytes to read from
     * @param byteOrder the byte order to use
     *
     * @return the signed 32-bit integer
     *
     * @throws NullPointerException      if bytes or byteOrder is null
     * @throws IndexOutOfBoundsException if there are fewer than four bytes available
     * @since 1.0.0
     */
    public static int readInt(byte[] bytes, ByteOrder byteOrder) {
        return readInt(bytes, 0, byteOrder);
    }

    /**
     * Reads a signed 32-bit integer from the given absolute offset in the byte array.
     *
     * @param bytes     the bytes to read from
     * @param offset    the absolute offset to read from
     * @param byteOrder the byte order to use
     *
     * @return the signed 32-bit integer
     *
     * @throws NullPointerException      if bytes or byteOrder is null
     * @throws IndexOutOfBoundsException if there are fewer than four bytes available at the given offset
     * @since 1.0.0
     */
    public static int readInt(byte[] bytes, int offset, ByteOrder byteOrder) {
        ensureAvailable(bytes, offset, Integer.BYTES);
        Objects.requireNonNull(byteOrder, FIELD_NAME_BYTE_ORDER);

        if (byteOrder == ByteOrder.BIG_ENDIAN) {
            return (unsignedByte(bytes, offset) << 24)
                    | (unsignedByte(bytes, offset + 1) << 16)
                    | (unsignedByte(bytes, offset + 2) << 8)
                    | unsignedByte(bytes, offset + 3);
        }

        return unsignedByte(bytes, offset)
                | (unsignedByte(bytes, offset + 1) << 8)
                | (unsignedByte(bytes, offset + 2) << 16)
                | (unsignedByte(bytes, offset + 3) << 24);
    }

    /**
     * Reads an unsigned 32-bit integer from the start of the given byte array using the platform native byte order.
     *
     * @param bytes the bytes to read from
     *
     * @return the unsigned 32-bit integer as a long value
     *
     * @since 1.0.0
     */
    public static long readUnsignedInt(byte[] bytes) {
        return readUnsignedInt(bytes, ByteOrder.nativeOrder());
    }

    /**
     * Reads an unsigned 32-bit integer from the start of the given byte array.
     *
     * @param bytes     the bytes to read from
     * @param byteOrder the byte order to use
     *
     * @return the unsigned 32-bit integer as a long value
     *
     * @since 1.0.0
     */
    public static long readUnsignedInt(byte[] bytes, ByteOrder byteOrder) {
        return Integer.toUnsignedLong(readInt(bytes, byteOrder));
    }

    /**
     * Reads an unsigned 32-bit integer from the given absolute offset in the byte array.
     *
     * @param bytes     the bytes to read from
     * @param offset    the absolute offset to read from
     * @param byteOrder the byte order to use
     *
     * @return the unsigned 32-bit integer as a long value
     *
     * @since 1.0.0
     */
    public static long readUnsignedInt(byte[] bytes, int offset, ByteOrder byteOrder) {
        return Integer.toUnsignedLong(readInt(bytes, offset, byteOrder));
    }

    /**
     * Reads a signed 64-bit integer from the start of the given byte array using the platform native byte order.
     *
     * @param bytes the bytes to read from
     *
     * @return the signed 64-bit integer
     *
     * @throws NullPointerException      if bytes is null
     * @throws IndexOutOfBoundsException if there are fewer than eight bytes available
     * @since 1.0.0
     */
    public static long readLong(byte[] bytes) {
        return readLong(bytes, ByteOrder.nativeOrder());
    }

    /**
     * Reads a signed 64-bit integer from the start of the given byte array.
     *
     * @param bytes     the bytes to read from
     * @param byteOrder the byte order to use
     *
     * @return the signed 64-bit integer
     *
     * @throws NullPointerException      if bytes or byteOrder is null
     * @throws IndexOutOfBoundsException if there are fewer than eight bytes available
     * @since 1.0.0
     */
    public static long readLong(byte[] bytes, ByteOrder byteOrder) {
        return readLong(bytes, 0, byteOrder);
    }

    /**
     * Reads a signed 64-bit integer from the given absolute offset in the byte array.
     *
     * @param bytes     the bytes to read from
     * @param offset    the absolute offset to read from
     * @param byteOrder the byte order to use
     *
     * @return the signed 64-bit integer
     *
     * @throws NullPointerException      if bytes or byteOrder is null
     * @throws IndexOutOfBoundsException if there are fewer than eight bytes available at the given offset
     * @since 1.0.0
     */
    public static long readLong(byte[] bytes, int offset, ByteOrder byteOrder) {
        ensureAvailable(bytes, offset, Long.BYTES);
        Objects.requireNonNull(byteOrder, FIELD_NAME_BYTE_ORDER);

        return readLong(index -> unsignedByte(bytes, index), offset, byteOrder);
    }


    /**
     * Reads an unsigned 64-bit integer from the start of the given byte array using the platform native byte order.
     *
     * @param bytes the bytes to read from
     *
     * @return the unsigned 64-bit integer as a {@link BigInteger}
     *
     * @throws NullPointerException      if bytes is null
     * @throws IndexOutOfBoundsException if there are fewer than eight bytes available
     * @since 1.0.0
     */
    public static BigInteger readUnsignedLong(byte[] bytes) {
        return readUnsignedLong(bytes, ByteOrder.nativeOrder());
    }

    /**
     * Reads an unsigned 64-bit integer from the start of the given byte array.
     *
     * @param bytes     the bytes to read from
     * @param byteOrder the byte order to use
     *
     * @return the unsigned 64-bit integer as a {@link BigInteger}
     *
     * @throws NullPointerException      if bytes or byteOrder is null
     * @throws IndexOutOfBoundsException if there are fewer than eight bytes available
     * @since 1.0.0
     */
    public static BigInteger readUnsignedLong(byte[] bytes, ByteOrder byteOrder) {
        return readUnsignedLong(bytes, 0, byteOrder);
    }

    /**
     * Reads an unsigned 64-bit integer from the given absolute offset in the byte array.
     *
     * @param bytes     the bytes to read from
     * @param offset    the absolute offset to read from
     * @param byteOrder the byte order to use
     *
     * @return the unsigned 64-bit integer as a {@link BigInteger}
     *
     * @throws NullPointerException      if bytes or byteOrder is null
     * @throws IndexOutOfBoundsException if there are fewer than eight bytes available at the given offset
     * @since 1.0.0
     */
    public static BigInteger readUnsignedLong(byte[] bytes, int offset, ByteOrder byteOrder) {
        ensureAvailable(bytes, offset, Long.BYTES);
        Objects.requireNonNull(byteOrder, FIELD_NAME_BYTE_ORDER);

        return readUnsignedLong(index -> unsignedByte(bytes, index), offset, byteOrder);
    }

    /**
     * Reads a 32-bit floating-point value from the start of the given byte array using the platform native byte order.
     *
     * @param bytes the bytes to read from
     *
     * @return the float value
     *
     * @since 1.0.0
     */
    public static float readFloat(byte[] bytes) {
        return readFloat(bytes, ByteOrder.nativeOrder());
    }

    /**
     * Reads a 32-bit floating-point value from the start of the given byte array.
     *
     * @param bytes     the bytes to read from
     * @param byteOrder the byte order to use
     *
     * @return the float value
     *
     * @since 1.0.0
     */
    public static float readFloat(byte[] bytes, ByteOrder byteOrder) {
        return Float.intBitsToFloat(readInt(bytes, byteOrder));
    }

    /**
     * Reads a 32-bit floating-point value from the given absolute offset in the byte array.
     *
     * @param bytes     the bytes to read from
     * @param offset    the absolute offset to read from
     * @param byteOrder the byte order to use
     *
     * @return the float value
     *
     * @since 1.0.0
     */
    public static float readFloat(byte[] bytes, int offset, ByteOrder byteOrder) {
        return Float.intBitsToFloat(readInt(bytes, offset, byteOrder));
    }

    /**
     * Reads a 64-bit floating-point value from the start of the given byte array using the platform native byte order.
     *
     * @param bytes the bytes to read from
     *
     * @return the double value
     *
     * @since 1.0.0
     */
    public static double readDouble(byte[] bytes) {
        return readDouble(bytes, ByteOrder.nativeOrder());
    }

    /**
     * Reads a 64-bit floating-point value from the start of the given byte array.
     *
     * @param bytes     the bytes to read from
     * @param byteOrder the byte order to use
     *
     * @return the double value
     *
     * @since 1.0.0
     */
    public static double readDouble(byte[] bytes, ByteOrder byteOrder) {
        return Double.longBitsToDouble(readLong(bytes, byteOrder));
    }

    /**
     * Reads a 64-bit floating-point value from the given absolute offset in the byte array.
     *
     * @param bytes     the bytes to read from
     * @param offset    the absolute offset to read from
     * @param byteOrder the byte order to use
     *
     * @return the double value
     *
     * @since 1.0.0
     */
    public static double readDouble(byte[] bytes, int offset, ByteOrder byteOrder) {
        return Double.longBitsToDouble(readLong(bytes, offset, byteOrder));
    }

    /**
     * Reads the given byte array as a string decoded with the platform default charset.
     *
     * @param bytes the bytes to read from
     *
     * @return the decoded string
     *
     * @throws NullPointerException if bytes is null
     * @since 1.0.0
     */
    public static String readString(byte[] bytes) {
        Objects.requireNonNull(bytes, FIELD_NAME_BYTES);
        return readString(bytes, 0, bytes.length, Charset.defaultCharset());
    }

    /**
     * Reads the given byte array as a string decoded with the given charset.
     *
     * @param bytes   the bytes to read from
     * @param charset the charset to use when decoding the string
     *
     * @return the decoded string
     *
     * @throws NullPointerException if bytes or charset is null
     * @since 1.0.0
     */
    public static String readString(byte[] bytes, Charset charset) {
        Objects.requireNonNull(bytes, FIELD_NAME_BYTES);
        return readString(bytes, 0, bytes.length, charset);
    }

    /**
     * Reads {@code length} bytes from the start of the given byte array as a string decoded with the platform default charset.
     *
     * @param bytes  the bytes to read from
     * @param length the number of bytes to read
     *
     * @return the decoded string
     *
     * @throws NullPointerException      if bytes is null
     * @throws IndexOutOfBoundsException if there are fewer than {@code length} bytes available
     * @since 1.0.0
     */
    public static String readString(byte[] bytes, int length) {
        return readString(bytes, 0, length, Charset.defaultCharset());
    }

    /**
     * Reads {@code length} bytes from the start of the given byte array as a string decoded with the given charset.
     *
     * @param bytes   the bytes to read from
     * @param length  the number of bytes to read
     * @param charset the charset to use when decoding the string
     *
     * @return the decoded string
     *
     * @throws NullPointerException      if bytes or charset is null
     * @throws IndexOutOfBoundsException if there are fewer than {@code length} bytes available
     * @since 1.0.0
     */
    public static String readString(byte[] bytes, int length, Charset charset) {
        return readString(bytes, 0, length, charset);
    }

    /**
     * Reads {@code length} bytes from the given absolute offset in the byte array as a string decoded with the platform default charset.
     *
     * @param bytes  the bytes to read from
     * @param offset the absolute offset to read from
     * @param length the number of bytes to read
     *
     * @return the decoded string
     *
     * @throws NullPointerException      if bytes is null
     * @throws IndexOutOfBoundsException if there are fewer than {@code length} bytes available at the given offset
     * @since 1.0.0
     */
    public static String readString(byte[] bytes, int offset, int length) {
        return readString(bytes, offset, length, Charset.defaultCharset());
    }

    /**
     * Reads {@code length} bytes from the given absolute offset in the byte array as a string decoded with the given charset.
     *
     * @param bytes   the bytes to read from
     * @param offset  the absolute offset to read from
     * @param length  the number of bytes to read
     * @param charset the charset to use when decoding the string
     *
     * @return the decoded string
     *
     * @throws NullPointerException      if bytes or charset is null
     * @throws IndexOutOfBoundsException if there are fewer than {@code length} bytes available at the given offset
     * @since 1.0.0
     */
    public static String readString(byte[] bytes, int offset, int length, Charset charset) {
        ensureAvailable(bytes, offset, length);
        Objects.requireNonNull(charset, FIELD_NAME_CHARSET);

        return new String(bytes, offset, length, charset);
    }

    private static short readShort(ByteDataBuffer.ByteBufferReader reader, int relativeOffset) {
        if (reader.byteOrder() == ByteOrder.BIG_ENDIAN) {
            return (short) ((unsignedByte(reader, relativeOffset) << 8)
                    | unsignedByte(reader, relativeOffset + 1));
        }

        return (short) (unsignedByte(reader, relativeOffset)
                | (unsignedByte(reader, relativeOffset + 1) << 8));
    }

    private static int readInt(ByteDataBuffer.ByteBufferReader reader, int relativeOffset) {
        if (reader.byteOrder() == ByteOrder.BIG_ENDIAN) {
            return (unsignedByte(reader, relativeOffset) << 24)
                    | (unsignedByte(reader, relativeOffset + 1) << 16)
                    | (unsignedByte(reader, relativeOffset + 2) << 8)
                    | unsignedByte(reader, relativeOffset + 3);
        }

        return unsignedByte(reader, relativeOffset)
                | (unsignedByte(reader, relativeOffset + 1) << 8)
                | (unsignedByte(reader, relativeOffset + 2) << 16)
                | (unsignedByte(reader, relativeOffset + 3) << 24);
    }

    private static long readLong(IntUnaryOperator unsignedByteAt, int offset, ByteOrder byteOrder) {
        if (byteOrder == ByteOrder.BIG_ENDIAN) {
            return ((long) unsignedByteAt.applyAsInt(offset) << 56)
                    | ((long) unsignedByteAt.applyAsInt(offset + 1) << 48)
                    | ((long) unsignedByteAt.applyAsInt(offset + 2) << 40)
                    | ((long) unsignedByteAt.applyAsInt(offset + 3) << 32)
                    | ((long) unsignedByteAt.applyAsInt(offset + 4) << 24)
                    | ((long) unsignedByteAt.applyAsInt(offset + 5) << 16)
                    | ((long) unsignedByteAt.applyAsInt(offset + 6) << 8)
                    | unsignedByteAt.applyAsInt(offset + 7);
        }

        return unsignedByteAt.applyAsInt(offset)
                | ((long) unsignedByteAt.applyAsInt(offset + 1) << 8)
                | ((long) unsignedByteAt.applyAsInt(offset + 2) << 16)
                | ((long) unsignedByteAt.applyAsInt(offset + 3) << 24)
                | ((long) unsignedByteAt.applyAsInt(offset + 4) << 32)
                | ((long) unsignedByteAt.applyAsInt(offset + 5) << 40)
                | ((long) unsignedByteAt.applyAsInt(offset + 6) << 48)
                | ((long) unsignedByteAt.applyAsInt(offset + 7) << 56);
    }

    private static BigInteger readUnsignedLong(IntUnaryOperator unsignedByteAt, int offset, ByteOrder byteOrder) {
        byte[] bytes = new byte[Long.BYTES];

        if (byteOrder == ByteOrder.BIG_ENDIAN) {
            for (int i = 0; i < Long.BYTES; i++) {
                bytes[i] = (byte) unsignedByteAt.applyAsInt(offset + i);
            }
        } else {
            for (int i = 0; i < Long.BYTES; i++) {
                bytes[i] = (byte) unsignedByteAt.applyAsInt(offset + Long.BYTES - 1 - i);
            }
        }

        return new BigInteger(1, bytes);
    }

    private static float readFloat(ByteDataBuffer.ByteBufferReader reader, int relativeOffset) {
        return Float.intBitsToFloat(readInt(reader, relativeOffset));
    }

    private static double readDouble(ByteDataBuffer.ByteBufferReader reader, int relativeOffset) {
        return Double.longBitsToDouble(readLong(index -> unsignedByte(reader, index), relativeOffset, reader.byteOrder()));
    }

    private static int unsignedByte(ByteDataBuffer.ByteBufferReader reader, int relativeOffset) {
        return Byte.toUnsignedInt(reader.peekByte(relativeOffset));
    }

    private static int unsignedByte(byte[] bytes, int offset) {
        return Byte.toUnsignedInt(bytes[offset]);
    }

    private void ensureAvailable(int relativeOffset, int length) {
        if (relativeOffset < 0 || !reader.ensureAvailable(reader.offset() + relativeOffset, length)) {
            throw new IndexOutOfBoundsException(
                    "Requested " + length + " byte(s) at relative offset " + relativeOffset
                            + ", current offset is " + reader.offset()
                            + " and remaining bytes are " + reader.remaining()
            );
        }
    }

    private static void ensureAvailable(byte[] bytes, int offset, int length) {
        Objects.requireNonNull(bytes, FIELD_NAME_BYTES);

        if (offset < 0 || length < 0 || offset > bytes.length - length) {
            throw new IndexOutOfBoundsException(
                    "Requested " + length + " byte(s) at offset " + offset
                            + ", but array length is " + bytes.length
            );
        }
    }
}