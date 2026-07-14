package eu.oberon.oss.tools.tsc.serz.util;

import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Objects;

/**
 * ByteDataBuffer is a utility class that provides buffering capabilities for byte arrays with support for reading data using a specified byte order.
 * <p>
 * It allows the creation of readers to interpret the contents of the buffer with either the platform's native byte order or a specified byte order.
 *
 * @author TigerLilly64
 * @since 1.0.0
 */
public final class ByteDataBuffer {
    private static final String FIELD_NAME_LENGTH = "length";
    private static final String FIELD_NAME_OFFSET = "offset";

    private final byte[] data;

    /**
     * Creates a new buffer with the given data using the platform native byte order.
     *
     * @param data the data to buffer
     *
     * @throws NullPointerException if the data is null
     * @since 1.0.0
     */
    public ByteDataBuffer(byte[] data) {
        this.data = Arrays.copyOf(Objects.requireNonNull(data, "data"), data.length);
    }

    /**
     * Returns the size of the buffer.
     *
     * @return the size of the buffer
     *
     * @since 1.0.0
     */
    public int size() {
        return data.length;
    }


    /**
     * Returns a reader for this buffer with the platform native byte order.
     *
     * @return a reader for this buffer
     *
     * @since 1.0.0
     */
    public ByteBufferReader reader() {
        return new ByteBufferReader(data, ByteOrder.nativeOrder());
    }

    /**
     * Returns a reader for this buffer with the specified byte order.
     *
     * @param byteOrder the byte order to use when reading primitive numeric values
     *
     * @return a reader for this buffer
     *
     * @throws NullPointerException if the byte order is null
     * @since 1.0.0
     */
    public ByteBufferReader reader(ByteOrder byteOrder) {
        return new ByteBufferReader(data, byteOrder);
    }

    // ... existing code ...

    /**
     * A utility class for reading sequential and random-access data from a byte buffer.
     * <p>
     * This reader provides byte-level access, cursor movement, matching, availability checks, and string decoding. Primitive numeric reads are provided by
     * {@link BaseJaveTypesBufferReader}.
     * <p>
     * The data is read from an internal byte array, and the read position is managed using an offset.
     *
     * @author TigerLilly64
     * @since 1.0.0
     */
    public static class ByteBufferReader {
        private final byte[] data;
        private int offset = 0;
        private final ByteOrder byteOrder;

        private ByteBufferReader(byte[] data, ByteOrder byteOrder) {
            this.data = Objects.requireNonNull(data, "data");
            this.byteOrder = Objects.requireNonNull(byteOrder, "byteOrder");
        }

        /**
         * Returns the byte order used when reading primitive numeric values.
         *
         * @return the byte order used by this buffer
         *
         * @since 1.0.0
         */
        public ByteOrder byteOrder() {
            return byteOrder;
        }

        /**
         * Returns the current offset of the buffer.
         *
         * @return the current offset of the buffer
         *
         * @since 1.0.0
         */
        public int offset() {
            return offset;
        }

        /**
         * Returns the remaining bytes in the buffer.
         *
         * @return the remaining bytes in the buffer
         *
         * @since 1.0.0
         */
        public int remaining() {
            return data.length - offset;
        }

        /**
         * Returns true if there are remaining bytes in the buffer.
         *
         * @return true if there are remaining bytes in the buffer
         *
         * @since 1.0.0
         */
        public boolean hasRemaining() {
            return remaining() > 0;
        }

        /**
         * Returns the next byte in the buffer.
         *
         * @return the next byte in the buffer
         *
         * @throws IndexOutOfBoundsException if there are no remaining bytes in the buffer
         * @since 1.0.0
         */
        public byte getByte() {
            ensureAvailable(offset, 1, true);
            return data[offset++];
        }

        /**
         * Returns the next {@code length} bytes in the buffer.
         *
         * @param length the number of bytes to read
         *
         * @return the next {@code length} bytes in the buffer
         *
         * @throws IllegalArgumentException  if the length is negative
         * @throws IndexOutOfBoundsException if there are insufficient remaining bytes in the buffer
         * @since 1.0.0
         */
        public byte[] getBytes(int length) {
            ensureNonNegativeValue(length, FIELD_NAME_LENGTH);
            ensureAvailable(offset, length, true);

            byte[] result = Arrays.copyOfRange(data, offset, offset + length);
            offset += length;
            return result;
        }

        /**
         * Returns the next byte in the buffer without advancing the offset.
         *
         * @return the next byte in the buffer
         *
         * @throws IndexOutOfBoundsException if there are no remaining bytes in the buffer
         * @since 1.0.0
         */
        public byte peekByte() {
            ensureAvailable(offset, 1, true);
            return data[offset];
        }

        /**
         * Returns the next {@code length} bytes in the buffer without advancing the offset.
         *
         * @param length the number of bytes to read
         *
         * @return the next {@code length} bytes in the buffer
         *
         * @throws IllegalArgumentException  if the length is negative
         * @throws IndexOutOfBoundsException if there are insufficient remaining bytes in the buffer
         * @since 1.0.0
         */
        public byte[] peekBytes(int length) {
            ensureNonNegativeValue(length, FIELD_NAME_LENGTH);
            ensureAvailable(offset, length, true);

            return Arrays.copyOfRange(data, offset, offset + length);
        }

        /**
         * Returns the byte at the given relative offset without advancing the offset.
         *
         * @param relativeOffset the offset relative to the current offset
         *
         * @return the byte at the given relative offset
         *
         * @throws IllegalArgumentException  if the relative offset is negative
         * @throws IndexOutOfBoundsException if there is no byte at the requested relative offset
         * @since 1.0.0
         */
        public byte peekByte(int relativeOffset) {
            ensureNonNegativeValue(relativeOffset, FIELD_NAME_OFFSET);
            ensureAvailable(offset + relativeOffset, 1, true);

            return data[offset + relativeOffset];
        }

        /**
         * Returns {@code length} bytes at the given relative offset without advancing the offset.
         *
         * @param relativeOffset the offset relative to the current offset
         * @param length         the number of bytes to read
         *
         * @return the bytes at the given relative offset
         *
         * @throws IllegalArgumentException  if the relative offset or length is negative
         * @throws IndexOutOfBoundsException if there are insufficient remaining bytes in the buffer
         * @since 1.0.0
         */
        public byte[] peekBytes(int relativeOffset, int length) {
            ensureNonNegativeValue(relativeOffset, FIELD_NAME_OFFSET);
            ensureNonNegativeValue(length, FIELD_NAME_LENGTH);
            ensureAvailable(offset + relativeOffset, length, true);

            return Arrays.copyOfRange(data, offset + relativeOffset, offset + relativeOffset + length);
        }

        /**
         * Checks if the next {@code expected.length} bytes in the buffer match the given expected bytes.
         *
         * @param expected the expected bytes to match
         *
         * @return {@code true} if the next bytes match the expected bytes, {@code false} otherwise
         *
         * @throws NullPointerException if the expected bytes are null
         * @since 1.0.0
         */
        public boolean matches(byte[] expected) {
            return matches(offset, expected);
        }

        /**
         * Checks if the data at the specified absolute offset matches the given expected bytes.
         *
         * @param offset   the absolute offset in the data buffer to start the comparison
         * @param expected the expected bytes to match
         *
         * @return {@code true} if the bytes at the specified offset match the expected bytes, {@code false} otherwise
         *
         * @throws IllegalArgumentException if the offset is negative
         * @throws NullPointerException     if the expected byte array is null
         * @since 1.0.0
         */
        public boolean matches(int offset, byte[] expected) {
            ensureNonNegativeValue(offset, FIELD_NAME_OFFSET);
            Objects.requireNonNull(expected, "expected");

            if (!ensureAvailable(offset, expected.length)) {
                return false;
            }

            for (int i = 0; i < expected.length; i++) {
                if (data[offset + i] != expected[i]) {
                    return false;
                }
            }

            return true;
        }

        /**
         * Rewinds the buffer to the beginning of the data buffer.
         *
         * @since 1.0.0
         */
        public void rewind() {
            offset = 0;
        }

        /**
         * Skips the given number of bytes in the buffer.
         *
         * @param delta the number of bytes to skip. Both positive and negative values are allowed, as long as they do not move the offset outside the valid
         *              range.
         *
         * @throws IndexOutOfBoundsException if the resulting offset would be outside the buffer
         * @since 1.0.0
         */
        public void skip(int delta) {
            int newOffset = offset + delta;

            if (newOffset < 0 || newOffset > data.length) {
                throw new IndexOutOfBoundsException(
                        "Cannot move offset by " + delta
                                + " from " + offset
                                + "; valid offset range is 0.." + data.length
                );
            }
            offset = newOffset;
        }

        /**
         * Ensures that the buffer has enough data available at the specified absolute offset and length.
         *
         * @param offset the absolute offset where data availability needs to be checked
         * @param length the number of bytes to check for availability
         *
         * @return true if the specified range of data is available, false otherwise
         *
         * @since 1.0.0
         */
        public boolean ensureAvailable(int offset, int length) {
            return ensureAvailable(offset, length, false);
        }

        /**
         * Ensures that the buffer has enough data available at the current offset for the specified length.
         *
         * @param length the number of bytes to check for availability
         *
         * @return true if the specified range of data is available, false otherwise
         *
         * @since 1.0.0
         */
        public boolean ensureAvailable(int length) {
            return ensureAvailable(offset, length, false);
        }

        /**
         * Returns the next {@code length} bytes in the buffer as a string decoded with the platform default charset and advances the offset.
         *
         * @param length the number of bytes to read
         *
         * @return the decoded string
         *
         * @throws IllegalArgumentException  if the length is negative
         * @throws IndexOutOfBoundsException if there are insufficient remaining bytes in the buffer
         * @since 1.0.0
         */
        public String getString(int length) {
            return getString(length, Charset.defaultCharset());
        }

        /**
         * Returns the next {@code length} bytes in the buffer as a string decoded with the given charset and advances the offset.
         *
         * @param length  the number of bytes to read
         * @param charset the charset to use when decoding the string
         *
         * @return the decoded string
         *
         * @throws IllegalArgumentException  if the length is negative
         * @throws IndexOutOfBoundsException if there are insufficient remaining bytes in the buffer
         * @throws NullPointerException      if the charset is null
         * @since 1.0.0
         */
        public String getString(int length, Charset charset) {
            String result = peekString(length, charset);
            offset += length;
            return result;
        }

        /**
         * Returns the next {@code length} bytes in the buffer as a string decoded with the platform default charset without advancing the offset.
         *
         * @param length the number of bytes to read
         *
         * @return the decoded string
         *
         * @throws IllegalArgumentException  if the length is negative
         * @throws IndexOutOfBoundsException if there are insufficient remaining bytes in the buffer
         * @since 1.0.0
         */
        public String peekString(int length) {
            return peekString(0, length, Charset.defaultCharset());
        }

        /**
         * Returns the next {@code length} bytes in the buffer as a string decoded with the given charset without advancing the offset.
         *
         * @param length  the number of bytes to read
         * @param charset the charset to use when decoding the string
         *
         * @return the decoded string
         *
         * @throws IllegalArgumentException  if the length is negative
         * @throws IndexOutOfBoundsException if there are insufficient remaining bytes in the buffer
         * @throws NullPointerException      if the charset is null
         * @since 1.0.0
         */
        public String peekString(int length, Charset charset) {
            return peekString(0, length, charset);
        }

        /**
         * Returns {@code length} bytes at the given relative offset as a string decoded with the platform default charset without advancing the offset.
         *
         * @param relativeOffset the offset relative to the current offset
         * @param length         the number of bytes to read
         *
         * @return the decoded string
         *
         * @throws IllegalArgumentException  if the relative offset or length is negative
         * @throws IndexOutOfBoundsException if there are insufficient remaining bytes in the buffer
         * @since 1.0.0
         */
        public String peekString(int relativeOffset, int length) {
            return peekString(relativeOffset, length, Charset.defaultCharset());
        }

        /**
         * Returns {@code length} bytes at the given relative offset as a string decoded with the given charset without advancing the offset.
         *
         * @param relativeOffset the offset relative to the current offset
         * @param length         the number of bytes to read
         * @param charset        the charset to use when decoding the string
         *
         * @return the decoded string
         *
         * @throws IllegalArgumentException  if the relative offset or length is negative
         * @throws IndexOutOfBoundsException if there are insufficient remaining bytes in the buffer
         * @throws NullPointerException      if the charset is null
         * @since 1.0.0
         */
        public String peekString(int relativeOffset, int length, Charset charset) {
            ensureNonNegativeValue(relativeOffset, FIELD_NAME_OFFSET);
            ensureNonNegativeValue(length, FIELD_NAME_LENGTH);
            Objects.requireNonNull(charset, "charset");
            ensureAvailable(offset + relativeOffset, length, true);

            return new String(data, offset + relativeOffset, length, charset);
        }

        /**
         * Ensures that the buffer has enough data available at the given absolute offset.
         *
         * @param startOffset    the absolute offset to check
         * @param length         the length of data to check
         * @param throwException whether to throw an exception if the data is not available
         *
         * @return true if the data is available, false otherwise
         *
         * @since 1.0.0
         */
        private boolean ensureAvailable(int startOffset, int length, boolean throwException) {
            if (startOffset < 0 || length < 0 || startOffset > data.length - length) {
                if (throwException) {
                    throw new IndexOutOfBoundsException(
                            "Requested " + length + " byte(s) at offset " + startOffset
                                    + ", but buffer size is " + data.length
                                    + " and current offset is " + offset
                    );
                }
                return false;
            }
            return true;
        }

        /**
         * Ensures that the specified value is non-negative.
         *
         * @param value     the value to validate
         * @param fieldName the name of the field associated with the value, used in the exception message
         *
         * @throws IllegalArgumentException if the value is negative
         */
        private static void ensureNonNegativeValue(int value, String fieldName) {
            if (value < 0) {
                throw new IllegalArgumentException(fieldName + " must not be negative: " + value);
            }
        }
    }
}