package eu.oberon.oss.tools.tsc.serz.util;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class ByteDataBufferTest {

    private static final byte[] PATTERN = new byte[]{
            0x01,
            0x23,
            0x45,
            0x67,
            (byte) 0x89,
            (byte) 0xAB,
            (byte) 0xCD,
            (byte) 0xEF
    };

    @Nested
    class Construction {

        @Test
        void constructorRejectsNullData() {
            assertThrows(NullPointerException.class, () -> new ByteDataBuffer(null));
        }

        @Test
        void constructorCopiesInputArrayDefensively() {
            byte[] source = new byte[]{0x01, 0x02, 0x03};
            ByteDataBuffer buffer = new ByteDataBuffer(source);

            source[0] = 0x55;

            assertEquals(0x01, buffer.reader().getByte());
        }

        @Test
        void sizeReturnsNumberOfBufferedBytes() {
            assertEquals(0, new ByteDataBuffer(new byte[0]).size());
            assertEquals(3, new ByteDataBuffer(new byte[]{0x01, 0x02, 0x03}).size());
        }

        @Test
        void readerUsesNativeByteOrderByDefault() {
            ByteDataBuffer.ByteBufferReader reader = new ByteDataBuffer(PATTERN).reader();

            assertEquals(ByteOrder.nativeOrder(), reader.byteOrder());
        }

        @Test
        void readerUsesGivenByteOrder() {
            ByteDataBuffer buffer = new ByteDataBuffer(PATTERN);

            assertEquals(ByteOrder.BIG_ENDIAN, buffer.reader(ByteOrder.BIG_ENDIAN).byteOrder());
            assertEquals(ByteOrder.LITTLE_ENDIAN, buffer.reader(ByteOrder.LITTLE_ENDIAN).byteOrder());
        }

        @Test
        void readerRejectsNullByteOrder() {
            assertThrows(NullPointerException.class, () -> new ByteDataBuffer(PATTERN).reader(null));
        }

        @Test
        void eachReaderHasIndependentOffset() {
            ByteDataBuffer buffer = new ByteDataBuffer(PATTERN);
            ByteDataBuffer.ByteBufferReader first = buffer.reader(ByteOrder.BIG_ENDIAN);
            ByteDataBuffer.ByteBufferReader second = buffer.reader(ByteOrder.BIG_ENDIAN);

            assertEquals(0x01, first.getByte());

            assertEquals(1, first.offset());
            assertEquals(0, second.offset());
            assertEquals(0x01, second.peekByte());
        }
    }

    @Nested
    class CursorState {

        @Test
        void newReaderStartsAtOffsetZeroWithAllBytesRemaining() {
            ByteDataBuffer.ByteBufferReader reader = new ByteDataBuffer(PATTERN).reader();

            assertEquals(0, reader.offset());
            assertEquals(PATTERN.length, reader.remaining());
            assertTrue(reader.hasRemaining());
        }

        @Test
        void emptyReaderHasNoRemainingBytes() {
            ByteDataBuffer.ByteBufferReader reader = new ByteDataBuffer(new byte[0]).reader();

            assertEquals(0, reader.offset());
            assertEquals(0, reader.remaining());
            assertFalse(reader.hasRemaining());
        }

        @Test
        void remainingAndHasRemainingReflectOffsetChanges() {
            ByteDataBuffer.ByteBufferReader reader = new ByteDataBuffer(PATTERN).reader();

            reader.skip(PATTERN.length - 1);

            assertEquals(PATTERN.length - 1, reader.offset());
            assertEquals(1, reader.remaining());
            assertTrue(reader.hasRemaining());

            reader.skip(1);

            assertEquals(PATTERN.length, reader.offset());
            assertEquals(0, reader.remaining());
            assertFalse(reader.hasRemaining());
        }

        @Test
        void rewindResetsOffsetToBeginning() {
            ByteDataBuffer.ByteBufferReader reader = new ByteDataBuffer(PATTERN).reader();

            reader.skip(4);
            reader.rewind();

            assertEquals(0, reader.offset());
            assertEquals(PATTERN.length, reader.remaining());
            assertEquals(0x01, reader.peekByte());
        }

        @Test
        void rewindIsSafeAtBeginningAndEnd() {
            ByteDataBuffer.ByteBufferReader reader = new ByteDataBuffer(PATTERN).reader();

            reader.rewind();
            assertEquals(0, reader.offset());

            reader.skip(PATTERN.length);
            reader.rewind();

            assertEquals(0, reader.offset());
        }
    }

    @Nested
    class Skip {

        @Test
        void skipMovesForwardAndBackwardWithinBuffer() {
            ByteDataBuffer.ByteBufferReader reader = new ByteDataBuffer(PATTERN).reader();

            reader.skip(5);
            assertEquals(5, reader.offset());
            assertEquals((byte) 0xAB, reader.peekByte());

            reader.skip(-3);
            assertEquals(2, reader.offset());
            assertEquals(0x45, reader.peekByte());
        }

        @Test
        void skipZeroDoesNotChangeOffset() {
            ByteDataBuffer.ByteBufferReader reader = new ByteDataBuffer(PATTERN).reader();

            reader.skip(3);
            reader.skip(0);

            assertEquals(3, reader.offset());
        }

        @Test
        void skipAllowsMovingExactlyToStartAndEnd() {
            ByteDataBuffer.ByteBufferReader reader = new ByteDataBuffer(PATTERN).reader();

            reader.skip(PATTERN.length);
            assertEquals(PATTERN.length, reader.offset());
            assertFalse(reader.hasRemaining());

            reader.skip(-PATTERN.length);
            assertEquals(0, reader.offset());
            assertTrue(reader.hasRemaining());
        }

        @Test
        void skipRejectsMovingBeforeStart() {
            ByteDataBuffer.ByteBufferReader reader = new ByteDataBuffer(PATTERN).reader();

            IndexOutOfBoundsException exception = assertThrows(
                    IndexOutOfBoundsException.class,
                    () -> reader.skip(-1)
            );

            assertEquals(0, reader.offset());
            assertTrue(exception.getMessage().contains("Cannot move offset by -1"));
            assertTrue(exception.getMessage().contains("valid offset range is 0.." + PATTERN.length));
        }

        @Test
        void skipRejectsMovingAfterEnd() {
            ByteDataBuffer.ByteBufferReader reader = new ByteDataBuffer(PATTERN).reader();

            IndexOutOfBoundsException exception = assertThrows(
                    IndexOutOfBoundsException.class,
                    () -> reader.skip(PATTERN.length + 1)
            );

            assertEquals(0, reader.offset());
            assertTrue(exception.getMessage().contains("Cannot move offset by " + (PATTERN.length + 1)));
            assertTrue(exception.getMessage().contains("valid offset range is 0.." + PATTERN.length));
        }

        @Test
        void skipRejectsBackwardMovePastStartFromNonZeroOffset() {
            ByteDataBuffer.ByteBufferReader reader = new ByteDataBuffer(PATTERN).reader();

            reader.skip(2);

            assertThrows(IndexOutOfBoundsException.class, () -> reader.skip(-3));
            assertEquals(2, reader.offset());
        }
    }

    @Nested
    class ByteReads {

        @Test
        void getByteReturnsNextByteAndAdvancesOffset() {
            ByteDataBuffer.ByteBufferReader reader = new ByteDataBuffer(PATTERN).reader();

            assertEquals(0x01, reader.getByte());
            assertEquals(1, reader.offset());

            assertEquals(0x23, reader.getByte());
            assertEquals(2, reader.offset());
        }

        @Test
        void getByteReturnsSignedByteValue() {
            ByteDataBuffer.ByteBufferReader reader = new ByteDataBuffer(new byte[]{(byte) 0xFF}).reader();

            assertEquals((byte) 0xFF, reader.getByte());
        }

        @Test
        void getByteRejectsReadAtEnd() {
            ByteDataBuffer.ByteBufferReader reader = new ByteDataBuffer(new byte[]{0x01}).reader();

            assertEquals(0x01, reader.getByte());

            assertThrows(IndexOutOfBoundsException.class, reader::getByte);
            assertEquals(1, reader.offset());
        }

        @Test
        void getByteRejectsReadFromEmptyBuffer() {
            ByteDataBuffer.ByteBufferReader reader = new ByteDataBuffer(new byte[0]).reader();

            assertThrows(IndexOutOfBoundsException.class, reader::getByte);
            assertEquals(0, reader.offset());
        }

        @Test
        void peekByteReturnsNextByteWithoutAdvancingOffset() {
            ByteDataBuffer.ByteBufferReader reader = new ByteDataBuffer(PATTERN).reader();

            assertEquals(0x01, reader.peekByte());
            assertEquals(0x01, reader.peekByte());
            assertEquals(0, reader.offset());
        }

        @Test
        void peekByteAtRelativeOffsetReturnsByteWithoutAdvancingOffset() {
            ByteDataBuffer.ByteBufferReader reader = new ByteDataBuffer(PATTERN).reader();

            reader.skip(1);

            assertEquals(0x45, reader.peekByte(1));
            assertEquals(1, reader.offset());
        }

        @Test
        void peekByteAtLastValidRelativeOffset() {
            ByteDataBuffer.ByteBufferReader reader = new ByteDataBuffer(PATTERN).reader();

            assertEquals((byte) 0xEF, reader.peekByte(PATTERN.length - 1));
            assertEquals(0, reader.offset());
        }

        @Test
        void peekByteRejectsNegativeRelativeOffset() {
            ByteDataBuffer.ByteBufferReader reader = new ByteDataBuffer(PATTERN).reader();

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> reader.peekByte(-1)
            );

            assertEquals(0, reader.offset());
            assertTrue(exception.getMessage().contains("offset must not be negative"));
        }

        @Test
        void peekByteRejectsRelativeOffsetAtEnd() {
            ByteDataBuffer.ByteBufferReader reader = new ByteDataBuffer(PATTERN).reader();

            assertThrows(IndexOutOfBoundsException.class, () -> reader.peekByte(PATTERN.length));
            assertEquals(0, reader.offset());
        }

        @Test
        void peekByteRejectsReadAtEnd() {
            ByteDataBuffer.ByteBufferReader reader = new ByteDataBuffer(PATTERN).reader();

            reader.skip(PATTERN.length);

            assertThrows(IndexOutOfBoundsException.class, reader::peekByte);
            assertEquals(PATTERN.length, reader.offset());
        }
    }

    @Nested
    class ByteArrayReads {

        @Test
        void getBytesReturnsRequestedBytesAndAdvancesOffset() {
            ByteDataBuffer.ByteBufferReader reader = new ByteDataBuffer(PATTERN).reader();

            assertArrayEquals(new byte[]{0x01, 0x23, 0x45}, reader.getBytes(3));
            assertEquals(3, reader.offset());

            assertArrayEquals(new byte[]{0x67, (byte) 0x89}, reader.getBytes(2));
            assertEquals(5, reader.offset());
        }

        @Test
        void getBytesLengthZeroReturnsEmptyArrayAndDoesNotAdvance() {
            ByteDataBuffer.ByteBufferReader reader = new ByteDataBuffer(PATTERN).reader();

            assertArrayEquals(new byte[0], reader.getBytes(0));
            assertEquals(0, reader.offset());
        }

        @Test
        void getBytesCanReadExactlyToEnd() {
            ByteDataBuffer.ByteBufferReader reader = new ByteDataBuffer(PATTERN).reader();

            assertArrayEquals(PATTERN, reader.getBytes(PATTERN.length));

            assertEquals(PATTERN.length, reader.offset());
            assertFalse(reader.hasRemaining());
        }

        @Test
        void getBytesReturnsDefensiveCopy() {
            ByteDataBuffer.ByteBufferReader reader = new ByteDataBuffer(PATTERN).reader();

            byte[] bytes = reader.getBytes(2);
            bytes[0] = 0x55;

            reader.rewind();
            assertEquals(0x01, reader.getByte());
        }

        @Test
        void getBytesRejectsNegativeLength() {
            ByteDataBuffer.ByteBufferReader reader = new ByteDataBuffer(PATTERN).reader();

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> reader.getBytes(-1)
            );

            assertEquals(0, reader.offset());
            assertTrue(exception.getMessage().contains("length must not be negative"));
        }

        @Test
        void getBytesRejectsInsufficientRemainingBytesAndDoesNotAdvance() {
            ByteDataBuffer.ByteBufferReader reader = new ByteDataBuffer(PATTERN).reader();

            reader.skip(PATTERN.length - 1);

            assertThrows(IndexOutOfBoundsException.class, () -> reader.getBytes(2));
            assertEquals(PATTERN.length - 1, reader.offset());
        }

        @Test
        void peekBytesReturnsRequestedBytesWithoutAdvancingOffset() {
            ByteDataBuffer.ByteBufferReader reader = new ByteDataBuffer(PATTERN).reader();

            assertArrayEquals(new byte[]{0x01, 0x23, 0x45}, reader.peekBytes(3));
            assertEquals(0, reader.offset());
        }

        @Test
        void peekBytesLengthZeroReturnsEmptyArrayWithoutAdvancing() {
            ByteDataBuffer.ByteBufferReader reader = new ByteDataBuffer(PATTERN).reader();

            assertArrayEquals(new byte[0], reader.peekBytes(0));
            assertEquals(0, reader.offset());
        }

        @Test
        void peekBytesAtRelativeOffsetReturnsBytesWithoutAdvancing() {
            ByteDataBuffer.ByteBufferReader reader = new ByteDataBuffer(PATTERN).reader();

            reader.skip(1);

            assertArrayEquals(new byte[]{0x45, 0x67, (byte) 0x89}, reader.peekBytes(1, 3));
            assertEquals(1, reader.offset());
        }

        @Test
        void peekBytesAtLastValidRelativeRange() {
            ByteDataBuffer.ByteBufferReader reader = new ByteDataBuffer(PATTERN).reader();

            assertArrayEquals(
                    new byte[]{(byte) 0xCD, (byte) 0xEF},
                    reader.peekBytes(PATTERN.length - 2, 2)
            );
            assertEquals(0, reader.offset());
        }

        @Test
        void peekBytesWithRelativeOffsetAndZeroLengthAtEndIsAllowed() {
            ByteDataBuffer.ByteBufferReader reader = new ByteDataBuffer(PATTERN).reader();

            assertArrayEquals(new byte[0], reader.peekBytes(PATTERN.length, 0));
            assertEquals(0, reader.offset());
        }

        @Test
        void peekBytesReturnsDefensiveCopy() {
            ByteDataBuffer.ByteBufferReader reader = new ByteDataBuffer(PATTERN).reader();

            byte[] bytes = reader.peekBytes(2);
            bytes[0] = 0x55;

            assertEquals(0x01, reader.peekByte());
        }

        @Test
        void peekBytesRejectsNegativeLength() {
            ByteDataBuffer.ByteBufferReader reader = new ByteDataBuffer(PATTERN).reader();

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> reader.peekBytes(-1)
            );

            assertTrue(exception.getMessage().contains("length must not be negative"));
            assertEquals(0, reader.offset());
        }

        @Test
        void peekBytesRejectsNegativeRelativeOffset() {
            ByteDataBuffer.ByteBufferReader reader = new ByteDataBuffer(PATTERN).reader();

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> reader.peekBytes(-1, 1)
            );

            assertTrue(exception.getMessage().contains("offset must not be negative"));
            assertEquals(0, reader.offset());
        }

        @Test
        void peekBytesRejectsNegativeLengthWithRelativeOffset() {
            ByteDataBuffer.ByteBufferReader reader = new ByteDataBuffer(PATTERN).reader();

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> reader.peekBytes(0, -1)
            );

            assertTrue(exception.getMessage().contains("length must not be negative"));
            assertEquals(0, reader.offset());
        }

        @Test
        void peekBytesRejectsInsufficientBytesAtCurrentOffset() {
            ByteDataBuffer.ByteBufferReader reader = new ByteDataBuffer(PATTERN).reader();

            reader.skip(PATTERN.length - 1);

            assertThrows(IndexOutOfBoundsException.class, () -> reader.peekBytes(2));
            assertEquals(PATTERN.length - 1, reader.offset());
        }

        @Test
        void peekBytesRejectsInsufficientBytesAtRelativeOffset() {
            ByteDataBuffer.ByteBufferReader reader = new ByteDataBuffer(PATTERN).reader();

            assertThrows(IndexOutOfBoundsException.class, () -> reader.peekBytes(PATTERN.length - 1, 2));
            assertEquals(0, reader.offset());
        }
    }

    @Nested
    class Matches {

        @Test
        void matchesCurrentOffsetReturnsTrueForMatchingBytes() {
            ByteDataBuffer.ByteBufferReader reader = new ByteDataBuffer(PATTERN).reader();

            assertTrue(reader.matches(new byte[]{0x01, 0x23, 0x45}));
        }

        @Test
        void matchesCurrentOffsetUsesCurrentOffset() {
            ByteDataBuffer.ByteBufferReader reader = new ByteDataBuffer(PATTERN).reader();

            reader.skip(2);

            assertTrue(reader.matches(new byte[]{0x45, 0x67}));
            assertFalse(reader.matches(new byte[]{0x01, 0x23}));
            assertEquals(2, reader.offset());
        }

        @Test
        void matchesAbsoluteOffsetReturnsTrueForMatchingBytes() {
            ByteDataBuffer.ByteBufferReader reader = new ByteDataBuffer(PATTERN).reader();

            assertTrue(reader.matches(2, new byte[]{0x45, 0x67, (byte) 0x89}));
            assertEquals(0, reader.offset());
        }

        @Test
        void matchesReturnsFalseForMismatchingBytes() {
            ByteDataBuffer.ByteBufferReader reader = new ByteDataBuffer(PATTERN).reader();

            assertFalse(reader.matches(new byte[]{0x01, 0x24}));
            assertFalse(reader.matches(2, new byte[]{0x45, 0x66}));
        }

        @Test
        void matchesReturnsFalseWhenExpectedBytesDoNotFit() {
            ByteDataBuffer.ByteBufferReader reader = new ByteDataBuffer(PATTERN).reader();

            reader.skip(PATTERN.length - 1);

            assertFalse(reader.matches(new byte[]{(byte) 0xEF, 0x00}));
            assertFalse(reader.matches(PATTERN.length - 1, new byte[]{(byte) 0xEF, 0x00}));
            assertEquals(PATTERN.length - 1, reader.offset());
        }

        @Test
        void matchesEmptyArrayIsTrueAtStartMiddleAndEnd() {
            ByteDataBuffer.ByteBufferReader reader = new ByteDataBuffer(PATTERN).reader();

            assertTrue(reader.matches(new byte[0]));

            reader.skip(3);
            assertTrue(reader.matches(new byte[0]));

            reader.skip(PATTERN.length - 3);
            assertTrue(reader.matches(new byte[0]));
            assertTrue(reader.matches(PATTERN.length, new byte[0]));
        }

        @Test
        void matchesRejectsNullExpectedAtCurrentOffset() {
            ByteDataBuffer.ByteBufferReader reader = new ByteDataBuffer(PATTERN).reader();

            assertThrows(NullPointerException.class, () -> reader.matches(null));
        }

        @Test
        void matchesRejectsNullExpectedAtAbsoluteOffset() {
            ByteDataBuffer.ByteBufferReader reader = new ByteDataBuffer(PATTERN).reader();

            assertThrows(NullPointerException.class, () -> reader.matches(0, null));
        }

        @Test
        void matchesRejectsNegativeAbsoluteOffset() {
            ByteDataBuffer.ByteBufferReader reader = new ByteDataBuffer(PATTERN).reader();

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> reader.matches(-1, new byte[]{0x01})
            );

            assertTrue(exception.getMessage().contains("offset must not be negative"));
            assertEquals(0, reader.offset());
        }

        @Test
        void matchesReturnsFalseWhenAbsoluteOffsetIsPastEnd() {
            ByteDataBuffer.ByteBufferReader reader = new ByteDataBuffer(PATTERN).reader();

            assertFalse(reader.matches(PATTERN.length + 1, new byte[0]));
            assertFalse(reader.matches(PATTERN.length + 1, new byte[]{0x01}));
        }
    }

    @Nested
    class AvailabilityChecks {

        @Test
        void ensureAvailableAtCurrentOffsetReturnsTrueWhenRangeFits() {
            ByteDataBuffer.ByteBufferReader reader = new ByteDataBuffer(PATTERN).reader();

            assertTrue(reader.ensureAvailable(0));
            assertTrue(reader.ensureAvailable(PATTERN.length));
        }

        @Test
        void ensureAvailableAtCurrentOffsetUsesCurrentOffset() {
            ByteDataBuffer.ByteBufferReader reader = new ByteDataBuffer(PATTERN).reader();

            reader.skip(2);

            assertTrue(reader.ensureAvailable(PATTERN.length - 2));
            assertFalse(reader.ensureAvailable(PATTERN.length - 1));
        }

        @Test
        void ensureAvailableAtAbsoluteOffsetReturnsTrueWhenRangeFits() {
            ByteDataBuffer.ByteBufferReader reader = new ByteDataBuffer(PATTERN).reader();

            assertTrue(reader.ensureAvailable(0, PATTERN.length));
            assertTrue(reader.ensureAvailable(2, PATTERN.length - 2));
            assertTrue(reader.ensureAvailable(PATTERN.length, 0));
        }

        @Test
        void ensureAvailableReturnsFalseForNegativeOffset() {
            ByteDataBuffer.ByteBufferReader reader = new ByteDataBuffer(PATTERN).reader();

            assertFalse(reader.ensureAvailable(-1, 0));
            assertFalse(reader.ensureAvailable(-1, 1));
        }

        @Test
        void ensureAvailableReturnsFalseForNegativeLength() {
            ByteDataBuffer.ByteBufferReader reader = new ByteDataBuffer(PATTERN).reader();

            assertFalse(reader.ensureAvailable(-1));
            assertFalse(reader.ensureAvailable(0, -1));
            assertFalse(reader.ensureAvailable(1, -1));
        }

        @Test
        void ensureAvailableReturnsFalseWhenRangeExceedsBuffer() {
            ByteDataBuffer.ByteBufferReader reader = new ByteDataBuffer(PATTERN).reader();

            assertFalse(reader.ensureAvailable(PATTERN.length + 1));
            assertFalse(reader.ensureAvailable(0, PATTERN.length + 1));
            assertFalse(reader.ensureAvailable(1, PATTERN.length));
            assertFalse(reader.ensureAvailable(PATTERN.length + 1, 0));
        }

        @Test
        void ensureAvailableDoesNotChangeOffset() {
            ByteDataBuffer.ByteBufferReader reader = new ByteDataBuffer(PATTERN).reader();

            reader.skip(3);

            assertTrue(reader.ensureAvailable(2));
            assertFalse(reader.ensureAvailable(PATTERN.length));
            assertTrue(reader.ensureAvailable(0, 1));
            assertFalse(reader.ensureAvailable(-1, 1));

            assertEquals(3, reader.offset());
        }

        @Test
        void throwingAvailabilityChecksIncludeUsefulMessageViaReadFailure() {
            ByteDataBuffer.ByteBufferReader reader = new ByteDataBuffer(PATTERN).reader();

            reader.skip(PATTERN.length);

            IndexOutOfBoundsException exception = assertThrows(IndexOutOfBoundsException.class, reader::getByte);

            assertAll(
                    () -> assertTrue(exception.getMessage().contains("Requested 1 byte(s)")),
                    () -> assertTrue(exception.getMessage().contains("offset " + PATTERN.length)),
                    () -> assertTrue(exception.getMessage().contains("buffer size is " + PATTERN.length)),
                    () -> assertTrue(exception.getMessage().contains("current offset is " + PATTERN.length))
            );
        }
    }

    @Nested
    class StringReads {

        @Test
        void getStringWithDefaultCharsetReturnsStringAndAdvancesOffset() {
            byte[] bytes = "hello".getBytes();
            ByteDataBuffer.ByteBufferReader reader = new ByteDataBuffer(bytes).reader();

            assertEquals("he", reader.getString(2));
            assertEquals(2, reader.offset());
        }

        @Test
        void getStringWithCharsetReturnsStringAndAdvancesOffset() {
            byte[] bytes = "Hello äöü".getBytes(StandardCharsets.UTF_8);
            ByteDataBuffer.ByteBufferReader reader = new ByteDataBuffer(bytes).reader();

            assertEquals("Hello", reader.getString(5, StandardCharsets.UTF_8));
            assertEquals(5, reader.offset());
        }

        @Test
        void getStringCanReadMultiByteCharactersWithCharset() {
            byte[] bytes = "äöü".getBytes(StandardCharsets.UTF_8);
            ByteDataBuffer.ByteBufferReader reader = new ByteDataBuffer(bytes).reader();

            assertEquals("äöü", reader.getString(bytes.length, StandardCharsets.UTF_8));
            assertEquals(bytes.length, reader.offset());
            assertFalse(reader.hasRemaining());
        }

        @Test
        void getStringLengthZeroReturnsEmptyStringAndDoesNotAdvance() {
            ByteDataBuffer.ByteBufferReader reader = new ByteDataBuffer("abc".getBytes(StandardCharsets.UTF_8)).reader();

            assertEquals("", reader.getString(0, StandardCharsets.UTF_8));
            assertEquals(0, reader.offset());
        }

        @Test
        void getStringRejectsNegativeLengthAndDoesNotAdvance() {
            ByteDataBuffer.ByteBufferReader reader = new ByteDataBuffer("abc".getBytes(StandardCharsets.UTF_8)).reader();

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> reader.getString(-1, StandardCharsets.UTF_8)
            );

            assertTrue(exception.getMessage().contains("length must not be negative"));
            assertEquals(0, reader.offset());
        }

        @Test
        void getStringRejectsNullCharsetAndDoesNotAdvance() {
            ByteDataBuffer.ByteBufferReader reader = new ByteDataBuffer("abc".getBytes(StandardCharsets.UTF_8)).reader();

            assertThrows(NullPointerException.class, () -> reader.getString(1, null));
            assertEquals(0, reader.offset());
        }

        @Test
        void getStringRejectsInsufficientRemainingBytesAndDoesNotAdvance() {
            ByteDataBuffer.ByteBufferReader reader = new ByteDataBuffer("abc".getBytes(StandardCharsets.UTF_8)).reader();

            reader.skip(2);

            assertThrows(IndexOutOfBoundsException.class, () -> reader.getString(2, StandardCharsets.UTF_8));
            assertEquals(2, reader.offset());
        }

        @Test
        void peekStringWithDefaultCharsetReturnsStringWithoutAdvancing() {
            byte[] bytes = "hello".getBytes();
            ByteDataBuffer.ByteBufferReader reader = new ByteDataBuffer(bytes).reader();

            assertEquals("he", reader.peekString(2));
            assertEquals(0, reader.offset());
        }

        @Test
        void peekStringWithCharsetReturnsStringWithoutAdvancing() {
            byte[] bytes = "Hello äöü".getBytes(StandardCharsets.UTF_8);
            ByteDataBuffer.ByteBufferReader reader = new ByteDataBuffer(bytes).reader();

            assertEquals("Hello", reader.peekString(5, StandardCharsets.UTF_8));
            assertEquals(0, reader.offset());
        }

        @Test
        void peekStringWithRelativeOffsetReturnsStringWithoutAdvancing() {
            byte[] bytes = "Hello World".getBytes(StandardCharsets.UTF_8);
            ByteDataBuffer.ByteBufferReader reader = new ByteDataBuffer(bytes).reader();

            reader.skip(1);

            assertEquals("World", reader.peekString(5, 5, StandardCharsets.UTF_8));
            assertEquals("World", reader.peekString(5, 5));
            assertEquals(1, reader.offset());
        }

        @Test
        void peekStringLengthZeroReturnsEmptyStringWithoutAdvancing() {
            ByteDataBuffer.ByteBufferReader reader = new ByteDataBuffer("abc".getBytes(StandardCharsets.UTF_8)).reader();

            assertEquals("", reader.peekString(0, StandardCharsets.UTF_8));
            assertEquals("", reader.peekString(2, 0, StandardCharsets.UTF_8));
            assertEquals(0, reader.offset());
        }

        @Test
        void peekStringWithRelativeOffsetAndZeroLengthAtEndIsAllowed() {
            byte[] bytes = "abc".getBytes(StandardCharsets.UTF_8);
            ByteDataBuffer.ByteBufferReader reader = new ByteDataBuffer(bytes).reader();

            assertEquals("", reader.peekString(bytes.length, 0, StandardCharsets.UTF_8));
            assertEquals(0, reader.offset());
        }

        @Test
        void peekStringRejectsNegativeLength() {
            ByteDataBuffer.ByteBufferReader reader = new ByteDataBuffer("abc".getBytes(StandardCharsets.UTF_8)).reader();

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> reader.peekString(-1, StandardCharsets.UTF_8)
            );

            assertTrue(exception.getMessage().contains("length must not be negative"));
            assertEquals(0, reader.offset());
        }

        @Test
        void peekStringRejectsNegativeRelativeOffset() {
            ByteDataBuffer.ByteBufferReader reader = new ByteDataBuffer("abc".getBytes(StandardCharsets.UTF_8)).reader();

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> reader.peekString(-1, 1, StandardCharsets.UTF_8)
            );

            assertTrue(exception.getMessage().contains("offset must not be negative"));
            assertEquals(0, reader.offset());
        }

        @Test
        void peekStringRejectsNegativeLengthWithRelativeOffset() {
            ByteDataBuffer.ByteBufferReader reader = new ByteDataBuffer("abc".getBytes(StandardCharsets.UTF_8)).reader();

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> reader.peekString(0, -1, StandardCharsets.UTF_8)
            );

            assertTrue(exception.getMessage().contains("length must not be negative"));
            assertEquals(0, reader.offset());
        }

        @Test
        void peekStringRejectsNullCharset() {
            ByteDataBuffer.ByteBufferReader reader = new ByteDataBuffer("abc".getBytes(StandardCharsets.UTF_8)).reader();

            assertThrows(NullPointerException.class, () -> reader.peekString(1, null));
            assertThrows(NullPointerException.class, () -> reader.peekString(0, 1, null));
            assertEquals(0, reader.offset());
        }

        @Test
        void peekStringRejectsInsufficientRemainingBytesAtCurrentOffset() {
            ByteDataBuffer.ByteBufferReader reader = new ByteDataBuffer("abc".getBytes(StandardCharsets.UTF_8)).reader();

            reader.skip(2);

            assertThrows(IndexOutOfBoundsException.class, () -> reader.peekString(2, StandardCharsets.UTF_8));
            assertEquals(2, reader.offset());
        }

        @Test
        void peekStringRejectsInsufficientBytesAtRelativeOffset() {
            ByteDataBuffer.ByteBufferReader reader = new ByteDataBuffer("abc".getBytes(StandardCharsets.UTF_8)).reader();

            assertThrows(IndexOutOfBoundsException.class, () -> reader.peekString(2, 2, StandardCharsets.UTF_8));
            assertEquals(0, reader.offset());
        }
    }
}