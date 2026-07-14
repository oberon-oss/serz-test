package eu.oberon.oss.tools.tsc.serz.util;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class BaseJaveTypesBufferReaderTest {

    private static final byte[] PATTERN = new byte[]{
            0x01,
            0x23,
            0x45,
            0x67,
            (byte) 0x89,
            (byte) 0xAB,
            (byte) 0xCD,
            (byte) 0xEF,
            0x10,
            0x32,
            0x54,
            0x76,
            (byte) 0x98,
            (byte) 0xBA,
            (byte) 0xDC,
            (byte) 0xFE
    };

    @Test
    void peekStringReadsAtCurrentAndRelativeOffsetsWithoutAdvancing() {
        byte[] bytes = "hello-world".getBytes(StandardCharsets.UTF_8);
        ByteDataBuffer.ByteBufferReader reader = new ByteDataBuffer(bytes).reader(ByteOrder.BIG_ENDIAN);
        BaseJaveTypesBufferReader primitiveReader = new BaseJaveTypesBufferReader(reader);

        assertEquals("hello", primitiveReader.peekString(5, StandardCharsets.UTF_8));
        assertEquals("world", primitiveReader.peekString(6, 5, StandardCharsets.UTF_8));

        assertEquals(0, reader.offset());
    }


    @Nested
    class Construction {

        @Test
        void constructorStoresWrappedReader() {
            ByteDataBuffer.ByteBufferReader reader = new ByteDataBuffer(PATTERN).reader(ByteOrder.BIG_ENDIAN);

            BaseJaveTypesBufferReader primitiveReader = new BaseJaveTypesBufferReader(reader);

            assertSame(reader, primitiveReader.reader());
        }

        @Test
        void constructorRejectsNullReader() {
            assertThrows(NullPointerException.class, () -> new BaseJaveTypesBufferReader(null));
        }
    }

    @Nested
    class StaticShortReads {

        @Test
        void readShortReadsFromStartInBothByteOrders() {
            assertEquals((short) 0x0123, BaseJaveTypesBufferReader.readShort(PATTERN, ByteOrder.BIG_ENDIAN));
            assertEquals((short) 0x2301, BaseJaveTypesBufferReader.readShort(PATTERN, ByteOrder.LITTLE_ENDIAN));
        }

        @Test
        void readShortReadsFromAbsoluteOffsetInBothByteOrders() {
            assertEquals((short) 0x4567, BaseJaveTypesBufferReader.readShort(PATTERN, 2, ByteOrder.BIG_ENDIAN));
            assertEquals((short) 0x6745, BaseJaveTypesBufferReader.readShort(PATTERN, 2, ByteOrder.LITTLE_ENDIAN));
        }

        @Test
        void readShortPreservesSignedValues() {
            byte[] bytes = new byte[]{(byte) 0x80, 0x00, 0x00, (byte) 0x80};

            assertEquals(Short.MIN_VALUE, BaseJaveTypesBufferReader.readShort(bytes, 0, ByteOrder.BIG_ENDIAN));
            assertEquals(Short.MIN_VALUE, BaseJaveTypesBufferReader.readShort(bytes, 2, ByteOrder.LITTLE_ENDIAN));
        }

        @Test
        void readUnsignedShortReadsFromStartInBothByteOrders() {
            byte[] bytes = new byte[]{(byte) 0xFF, (byte) 0xFE};

            assertEquals(65_534, BaseJaveTypesBufferReader.readUnsignedShort(bytes, ByteOrder.BIG_ENDIAN));
            assertEquals(65_279, BaseJaveTypesBufferReader.readUnsignedShort(bytes, ByteOrder.LITTLE_ENDIAN));
        }

        @Test
        void readUnsignedShortReadsFromAbsoluteOffsetInBothByteOrders() {
            byte[] bytes = new byte[]{0x00, (byte) 0xFF, (byte) 0xFF, 0x00};

            assertEquals(65_535, BaseJaveTypesBufferReader.readUnsignedShort(bytes, 1, ByteOrder.BIG_ENDIAN));
            assertEquals(255, BaseJaveTypesBufferReader.readUnsignedShort(bytes, 2, ByteOrder.LITTLE_ENDIAN));
        }

        @Test
        void readUnsignedShortCoversBoundaries() {
            assertEquals(0, BaseJaveTypesBufferReader.readUnsignedShort(new byte[]{0x00, 0x00}, ByteOrder.BIG_ENDIAN));
            assertEquals(65_535, BaseJaveTypesBufferReader.readUnsignedShort(new byte[]{(byte) 0xFF, (byte) 0xFF}, ByteOrder.BIG_ENDIAN));
        }
    }

    @Nested
    class StaticIntReads {

        @Test
        void readIntReadsFromStartInBothByteOrders() {
            assertEquals(0x01234567, BaseJaveTypesBufferReader.readInt(PATTERN, ByteOrder.BIG_ENDIAN));
            assertEquals(0x67452301, BaseJaveTypesBufferReader.readInt(PATTERN, ByteOrder.LITTLE_ENDIAN));
        }

        @Test
        void readIntReadsFromAbsoluteOffsetInBothByteOrders() {
            assertEquals(0x456789AB, BaseJaveTypesBufferReader.readInt(PATTERN, 2, ByteOrder.BIG_ENDIAN));
            assertEquals(0xAB896745, BaseJaveTypesBufferReader.readInt(PATTERN, 2, ByteOrder.LITTLE_ENDIAN));
        }

        @Test
        void readIntPreservesSignedValues() {
            byte[] bytes = new byte[]{
                    (byte) 0x80,
                    0x00,
                    0x00,
                    0x00,
                    0x00,
                    0x00,
                    0x00,
                    (byte) 0x80
            };

            assertEquals(Integer.MIN_VALUE, BaseJaveTypesBufferReader.readInt(bytes, 0, ByteOrder.BIG_ENDIAN));
            assertEquals(Integer.MIN_VALUE, BaseJaveTypesBufferReader.readInt(bytes, 4, ByteOrder.LITTLE_ENDIAN));
        }

        @Test
        void readUnsignedIntReadsFromStartInBothByteOrders() {
            byte[] bytes = new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFE};

            assertEquals(4_294_967_294L, BaseJaveTypesBufferReader.readUnsignedInt(bytes, ByteOrder.BIG_ENDIAN));
            assertEquals(4_278_190_079L, BaseJaveTypesBufferReader.readUnsignedInt(bytes, ByteOrder.LITTLE_ENDIAN));
        }

        @Test
        void readUnsignedIntReadsFromAbsoluteOffsetInBothByteOrders() {
            byte[] bytes = new byte[]{
                    0x00,
                    (byte) 0xFF,
                    (byte) 0xFF,
                    (byte) 0xFF,
                    (byte) 0xFF,
                    0x00
            };

            assertEquals(4_294_967_295L, BaseJaveTypesBufferReader.readUnsignedInt(bytes, 1, ByteOrder.BIG_ENDIAN));
            assertEquals(16_777_215L, BaseJaveTypesBufferReader.readUnsignedInt(bytes, 2, ByteOrder.LITTLE_ENDIAN));
        }

        @Test
        void readUnsignedIntCoversBoundaries() {
            assertEquals(0L, BaseJaveTypesBufferReader.readUnsignedInt(new byte[]{0x00, 0x00, 0x00, 0x00}, ByteOrder.BIG_ENDIAN));
            assertEquals(
                    4_294_967_295L,
                    BaseJaveTypesBufferReader.readUnsignedInt(
                            new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF},
                            ByteOrder.BIG_ENDIAN
                    )
            );
        }
    }

    @Nested
    class StaticLongReads {

        @Test
        void readLongReadsFromStartInBothByteOrders() {
            assertEquals(0x0123456789ABCDEFL, BaseJaveTypesBufferReader.readLong(PATTERN, ByteOrder.BIG_ENDIAN));
            assertEquals(0xEFCDAB8967452301L, BaseJaveTypesBufferReader.readLong(PATTERN, ByteOrder.LITTLE_ENDIAN));
        }

        @Test
        void readLongReadsFromAbsoluteOffsetInBothByteOrders() {
            assertEquals(0x456789ABCDEF1032L, BaseJaveTypesBufferReader.readLong(PATTERN, 2, ByteOrder.BIG_ENDIAN));
            assertEquals(0x3210EFCDAB896745L, BaseJaveTypesBufferReader.readLong(PATTERN, 2, ByteOrder.LITTLE_ENDIAN));
        }

        @Test
        void readLongPreservesSignedValues() {
            byte[] bytes = new byte[]{
                    (byte) 0x80,
                    0x00,
                    0x00,
                    0x00,
                    0x00,
                    0x00,
                    0x00,
                    0x00,
                    0x00,
                    0x00,
                    0x00,
                    0x00,
                    0x00,
                    0x00,
                    0x00,
                    (byte) 0x80
            };

            assertEquals(Long.MIN_VALUE, BaseJaveTypesBufferReader.readLong(bytes, 0, ByteOrder.BIG_ENDIAN));
            assertEquals(Long.MIN_VALUE, BaseJaveTypesBufferReader.readLong(bytes, 8, ByteOrder.LITTLE_ENDIAN));
        }

        @Test
        void readUnsignedLongReadsFromStartInBothByteOrders() {
            assertEquals(
                    new BigInteger("81985529216486895"),
                    BaseJaveTypesBufferReader.readUnsignedLong(PATTERN, ByteOrder.BIG_ENDIAN)
            );
            assertEquals(
                    new BigInteger("17279655951921914625"),
                    BaseJaveTypesBufferReader.readUnsignedLong(PATTERN, ByteOrder.LITTLE_ENDIAN)
            );
        }

        @Test
        void readUnsignedLongReadsFromAbsoluteOffsetInBothByteOrders() {
            assertEquals(
                    new BigInteger("5001117282205634610"),
                    BaseJaveTypesBufferReader.readUnsignedLong(PATTERN, 2, ByteOrder.BIG_ENDIAN)
            );
            assertEquals(
                    new BigInteger("3607646968149010245"),
                    BaseJaveTypesBufferReader.readUnsignedLong(PATTERN, 2, ByteOrder.LITTLE_ENDIAN)
            );
        }

        @Test
        void readUnsignedLongCoversUnsignedBoundaries() {
            assertEquals(
                    BigInteger.ZERO,
                    BaseJaveTypesBufferReader.readUnsignedLong(
                            new byte[]{0, 0, 0, 0, 0, 0, 0, 0},
                            ByteOrder.BIG_ENDIAN
                    )
            );
            assertEquals(
                    new BigInteger("18446744073709551615"),
                    BaseJaveTypesBufferReader.readUnsignedLong(
                            new byte[]{
                                    (byte) 0xFF,
                                    (byte) 0xFF,
                                    (byte) 0xFF,
                                    (byte) 0xFF,
                                    (byte) 0xFF,
                                    (byte) 0xFF,
                                    (byte) 0xFF,
                                    (byte) 0xFF
                            },
                            ByteOrder.BIG_ENDIAN
                    )
            );
        }
    }

    @Nested
    class StaticFloatingPointReads {

        @Test
        void readFloatReadsFromStartInBothByteOrders() {
            float expected = -123.5f;
            byte[] bigEndian = ByteBuffer.allocate(Float.BYTES)
                    .order(ByteOrder.BIG_ENDIAN)
                    .putFloat(expected)
                    .array();
            byte[] littleEndian = ByteBuffer.allocate(Float.BYTES)
                    .order(ByteOrder.LITTLE_ENDIAN)
                    .putFloat(expected)
                    .array();

            assertEquals(expected, BaseJaveTypesBufferReader.readFloat(bigEndian, ByteOrder.BIG_ENDIAN));
            assertEquals(expected, BaseJaveTypesBufferReader.readFloat(littleEndian, ByteOrder.LITTLE_ENDIAN));
        }

        @Test
        void readFloatReadsFromAbsoluteOffsetInBothByteOrders() {
            float expected = Float.MIN_NORMAL;
            byte[] bigEndian = withPadding(ByteBuffer.allocate(Float.BYTES)
                    .order(ByteOrder.BIG_ENDIAN)
                    .putFloat(expected)
                    .array());
            byte[] littleEndian = withPadding(ByteBuffer.allocate(Float.BYTES)
                    .order(ByteOrder.LITTLE_ENDIAN)
                    .putFloat(expected)
                    .array());

            assertEquals(expected, BaseJaveTypesBufferReader.readFloat(bigEndian, 2, ByteOrder.BIG_ENDIAN));
            assertEquals(expected, BaseJaveTypesBufferReader.readFloat(littleEndian, 2, ByteOrder.LITTLE_ENDIAN));
        }

        @Test
        void readFloatPreservesRawNanBits() {
            int bits = 0x7FC00001;
            byte[] bytes = ByteBuffer.allocate(Float.BYTES)
                    .order(ByteOrder.BIG_ENDIAN)
                    .putInt(bits)
                    .array();

            float value = BaseJaveTypesBufferReader.readFloat(bytes, ByteOrder.BIG_ENDIAN);

            assertTrue(Float.isNaN(value));
            assertEquals(bits, Float.floatToRawIntBits(value));
        }

        @Test
        void readDoubleReadsFromStartInBothByteOrders() {
            double expected = -Math.PI;
            byte[] bigEndian = ByteBuffer.allocate(Double.BYTES)
                    .order(ByteOrder.BIG_ENDIAN)
                    .putDouble(expected)
                    .array();
            byte[] littleEndian = ByteBuffer.allocate(Double.BYTES)
                    .order(ByteOrder.LITTLE_ENDIAN)
                    .putDouble(expected)
                    .array();

            assertEquals(expected, BaseJaveTypesBufferReader.readDouble(bigEndian, ByteOrder.BIG_ENDIAN));
            assertEquals(expected, BaseJaveTypesBufferReader.readDouble(littleEndian, ByteOrder.LITTLE_ENDIAN));
        }

        @Test
        void readDoubleReadsFromAbsoluteOffsetInBothByteOrders() {
            double expected = Double.MIN_NORMAL;
            byte[] bigEndian = withPadding(ByteBuffer.allocate(Double.BYTES)
                    .order(ByteOrder.BIG_ENDIAN)
                    .putDouble(expected)
                    .array());
            byte[] littleEndian = withPadding(ByteBuffer.allocate(Double.BYTES)
                    .order(ByteOrder.LITTLE_ENDIAN)
                    .putDouble(expected)
                    .array());

            assertEquals(expected, BaseJaveTypesBufferReader.readDouble(bigEndian, 2, ByteOrder.BIG_ENDIAN));
            assertEquals(expected, BaseJaveTypesBufferReader.readDouble(littleEndian, 2, ByteOrder.LITTLE_ENDIAN));
        }

        @Test
        void readDoublePreservesRawNanBits() {
            long bits = 0x7FF8000000000001L;
            byte[] bytes = ByteBuffer.allocate(Double.BYTES)
                    .order(ByteOrder.BIG_ENDIAN)
                    .putLong(bits)
                    .array();

            double value = BaseJaveTypesBufferReader.readDouble(bytes, ByteOrder.BIG_ENDIAN);

            assertTrue(Double.isNaN(value));
            assertEquals(bits, Double.doubleToRawLongBits(value));
        }
    }

    @Nested
    class StaticReadFailures {

        @Test
        void staticReadsRejectNullBytes() {
            assertThrows(NullPointerException.class, () -> BaseJaveTypesBufferReader.readShort(null, ByteOrder.BIG_ENDIAN));
            assertThrows(NullPointerException.class, () -> BaseJaveTypesBufferReader.readUnsignedShort(null, ByteOrder.BIG_ENDIAN));
            assertThrows(NullPointerException.class, () -> BaseJaveTypesBufferReader.readInt(null, ByteOrder.BIG_ENDIAN));
            assertThrows(NullPointerException.class, () -> BaseJaveTypesBufferReader.readUnsignedInt(null, ByteOrder.BIG_ENDIAN));
            assertThrows(NullPointerException.class, () -> BaseJaveTypesBufferReader.readLong(null, ByteOrder.BIG_ENDIAN));
            assertThrows(NullPointerException.class, () -> BaseJaveTypesBufferReader.readFloat(null, ByteOrder.BIG_ENDIAN));
            assertThrows(NullPointerException.class, () -> BaseJaveTypesBufferReader.readDouble(null, ByteOrder.BIG_ENDIAN));
        }

        @Test
        void staticReadsRejectNullByteOrder() {
            byte[] bytes = new byte[Long.BYTES];

            assertThrows(NullPointerException.class, () -> BaseJaveTypesBufferReader.readShort(bytes, null));
            assertThrows(NullPointerException.class, () -> BaseJaveTypesBufferReader.readUnsignedShort(bytes, null));
            assertThrows(NullPointerException.class, () -> BaseJaveTypesBufferReader.readInt(bytes, null));
            assertThrows(NullPointerException.class, () -> BaseJaveTypesBufferReader.readUnsignedInt(bytes, null));
            assertThrows(NullPointerException.class, () -> BaseJaveTypesBufferReader.readLong(bytes, null));
            assertThrows(NullPointerException.class, () -> BaseJaveTypesBufferReader.readFloat(bytes, null));
            assertThrows(NullPointerException.class, () -> BaseJaveTypesBufferReader.readDouble(bytes, null));
        }

        @Test
        void staticReadsRejectNegativeOffsets() {
            byte[] bytes = new byte[Long.BYTES];

            assertThrows(IndexOutOfBoundsException.class, () -> BaseJaveTypesBufferReader.readShort(bytes, -1, ByteOrder.BIG_ENDIAN));
            assertThrows(IndexOutOfBoundsException.class, () -> BaseJaveTypesBufferReader.readUnsignedShort(bytes, -1, ByteOrder.BIG_ENDIAN));
            assertThrows(IndexOutOfBoundsException.class, () -> BaseJaveTypesBufferReader.readInt(bytes, -1, ByteOrder.BIG_ENDIAN));
            assertThrows(IndexOutOfBoundsException.class, () -> BaseJaveTypesBufferReader.readUnsignedInt(bytes, -1, ByteOrder.BIG_ENDIAN));
            assertThrows(IndexOutOfBoundsException.class, () -> BaseJaveTypesBufferReader.readLong(bytes, -1, ByteOrder.BIG_ENDIAN));
            assertThrows(IndexOutOfBoundsException.class, () -> BaseJaveTypesBufferReader.readFloat(bytes, -1, ByteOrder.BIG_ENDIAN));
            assertThrows(IndexOutOfBoundsException.class, () -> BaseJaveTypesBufferReader.readDouble(bytes, -1, ByteOrder.BIG_ENDIAN));
        }

        @Test
        void staticReadsRejectInsufficientBytesAtOffset() {
            byte[] bytes = new byte[Long.BYTES];

            assertThrows(IndexOutOfBoundsException.class, () -> BaseJaveTypesBufferReader.readShort(bytes, Long.BYTES - 1, ByteOrder.BIG_ENDIAN));
            assertThrows(IndexOutOfBoundsException.class, () -> BaseJaveTypesBufferReader.readUnsignedShort(bytes, Long.BYTES - 1, ByteOrder.BIG_ENDIAN));
            assertThrows(IndexOutOfBoundsException.class, () -> BaseJaveTypesBufferReader.readInt(bytes, Long.BYTES - 3, ByteOrder.BIG_ENDIAN));
            assertThrows(IndexOutOfBoundsException.class, () -> BaseJaveTypesBufferReader.readUnsignedInt(bytes, Long.BYTES - 3, ByteOrder.BIG_ENDIAN));
            assertThrows(IndexOutOfBoundsException.class, () -> BaseJaveTypesBufferReader.readLong(bytes, 1, ByteOrder.BIG_ENDIAN));
            assertThrows(IndexOutOfBoundsException.class, () -> BaseJaveTypesBufferReader.readFloat(bytes, Long.BYTES - 3, ByteOrder.BIG_ENDIAN));
            assertThrows(IndexOutOfBoundsException.class, () -> BaseJaveTypesBufferReader.readDouble(bytes, 1, ByteOrder.BIG_ENDIAN));
        }

        @Test
        void staticReadsAcceptLastValidOffset() {
            byte[] bytes = new byte[]{0x00, 0x01, 0x23, 0x45, 0x67, (byte) 0x89, (byte) 0xAB, (byte) 0xCD, (byte) 0xEF};

            assertEquals((short) 0xCDEF, BaseJaveTypesBufferReader.readShort(bytes, bytes.length - Short.BYTES, ByteOrder.BIG_ENDIAN));
            assertEquals(0x89ABCDEFL, BaseJaveTypesBufferReader.readUnsignedInt(bytes, bytes.length - Integer.BYTES, ByteOrder.BIG_ENDIAN));
            assertEquals(0x0123456789ABCDEFL, BaseJaveTypesBufferReader.readLong(bytes, bytes.length - Long.BYTES, ByteOrder.BIG_ENDIAN));
        }
    }

    @Nested
    class PeekReads {

        @Test
        void noArgumentPeekMethodsReadAtCurrentOffsetWithoutAdvancing() {
            ByteDataBuffer.ByteBufferReader reader = new ByteDataBuffer(PATTERN).reader(ByteOrder.BIG_ENDIAN);
            BaseJaveTypesBufferReader primitiveReader = new BaseJaveTypesBufferReader(reader);

            assertEquals((short) 0x0123, primitiveReader.peekShort());
            assertEquals(0x0123, primitiveReader.peekUnsignedShort());
            assertEquals(0x01234567, primitiveReader.peekInt());
            assertEquals(0x01234567L, primitiveReader.peekUnsignedInt());
            assertEquals(0x0123456789ABCDEFL, primitiveReader.peekLong());
            assertEquals(new BigInteger("81985529216486895"), primitiveReader.peekUnsignedLong());

            assertEquals(0, reader.offset());
        }

        @Test
        void relativePeekMethodsReadFromCurrentOffsetPlusRelativeOffsetWithoutAdvancing() {
            ByteDataBuffer.ByteBufferReader reader = new ByteDataBuffer(PATTERN).reader(ByteOrder.BIG_ENDIAN);
            BaseJaveTypesBufferReader primitiveReader = new BaseJaveTypesBufferReader(reader);

            reader.skip(1);

            assertEquals((short) 0x4567, primitiveReader.peekShort(1));
            assertEquals(0x4567, primitiveReader.peekUnsignedShort(1));
            assertEquals(0x456789AB, primitiveReader.peekInt(1));
            assertEquals(0x456789ABL, primitiveReader.peekUnsignedInt(1));
            assertEquals(0x456789ABCDEF1032L, primitiveReader.peekLong(1));
            assertEquals(new BigInteger("5001117282205634610"), primitiveReader.peekUnsignedLong(1));

            assertEquals(1, reader.offset());
        }

        @Test
        void peekMethodsUseLittleEndianReaderOrder() {
            ByteDataBuffer.ByteBufferReader reader = new ByteDataBuffer(PATTERN).reader(ByteOrder.LITTLE_ENDIAN);
            BaseJaveTypesBufferReader primitiveReader = new BaseJaveTypesBufferReader(reader);

            assertEquals((short) 0x2301, primitiveReader.peekShort());
            assertEquals(0x67452301, primitiveReader.peekInt());
            assertEquals(0xEFCDAB8967452301L, primitiveReader.peekLong());

            assertEquals(0, reader.offset());
        }

        @Test
        void peekUnsignedMethodsCoverUnsignedBoundaries() {
            BaseJaveTypesBufferReader primitiveReader = new BaseJaveTypesBufferReader(
                    new ByteDataBuffer(new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF})
                            .reader(ByteOrder.BIG_ENDIAN)
            );

            assertEquals(65_535, primitiveReader.peekUnsignedShort());
            assertEquals(4_294_967_295L, primitiveReader.peekUnsignedInt());
        }

        @Test
        void peekFloatReadsAtCurrentAndRelativeOffsetsWithoutAdvancing() {
            float first = -123.5f;
            float second = Float.POSITIVE_INFINITY;
            byte[] bytes = ByteBuffer.allocate(Float.BYTES * 2)
                    .order(ByteOrder.LITTLE_ENDIAN)
                    .putFloat(first)
                    .putFloat(second)
                    .array();
            ByteDataBuffer.ByteBufferReader reader = new ByteDataBuffer(bytes).reader(ByteOrder.LITTLE_ENDIAN);
            BaseJaveTypesBufferReader primitiveReader = new BaseJaveTypesBufferReader(reader);

            assertEquals(first, primitiveReader.peekFloat());
            assertEquals(second, primitiveReader.peekFloat(Float.BYTES));

            assertEquals(0, reader.offset());
        }

        @Test
        void peekDoubleReadsAtCurrentAndRelativeOffsetsWithoutAdvancing() {
            double first = Math.E;
            double second = Double.NEGATIVE_INFINITY;
            byte[] bytes = ByteBuffer.allocate(Double.BYTES * 2)
                    .order(ByteOrder.LITTLE_ENDIAN)
                    .putDouble(first)
                    .putDouble(second)
                    .array();
            ByteDataBuffer.ByteBufferReader reader = new ByteDataBuffer(bytes).reader(ByteOrder.LITTLE_ENDIAN);
            BaseJaveTypesBufferReader primitiveReader = new BaseJaveTypesBufferReader(reader);

            assertEquals(first, primitiveReader.peekDouble());
            assertEquals(second, primitiveReader.peekDouble(Double.BYTES));

            assertEquals(0, reader.offset());
        }

        @Test
        void instanceReadShortReadsAtCurrentOffsetWithoutAdvancing() {
            ByteDataBuffer.ByteBufferReader reader = new ByteDataBuffer(PATTERN).reader(ByteOrder.BIG_ENDIAN);
            BaseJaveTypesBufferReader primitiveReader = new BaseJaveTypesBufferReader(reader);

            assertEquals((short) 0x0123, primitiveReader.readShort());

            assertEquals(0, reader.offset());
        }
    }

    @Nested
    class GetReads {

        @Test
        void getShortAdvancesByTwoBytes() {
            ByteDataBuffer.ByteBufferReader reader = new ByteDataBuffer(PATTERN).reader(ByteOrder.BIG_ENDIAN);
            BaseJaveTypesBufferReader primitiveReader = new BaseJaveTypesBufferReader(reader);

            assertEquals((short) 0x0123, primitiveReader.getShort());

            assertEquals(Short.BYTES, reader.offset());
        }

        @Test
        void getUnsignedShortAdvancesByTwoBytes() {
            ByteDataBuffer.ByteBufferReader reader = new ByteDataBuffer(new byte[]{(byte) 0xFF, (byte) 0xFF})
                    .reader(ByteOrder.BIG_ENDIAN);
            BaseJaveTypesBufferReader primitiveReader = new BaseJaveTypesBufferReader(reader);

            assertEquals(65_535, primitiveReader.getUnsignedShort());

            assertEquals(Short.BYTES, reader.offset());
        }

        @Test
        void getIntAdvancesByFourBytes() {
            ByteDataBuffer.ByteBufferReader reader = new ByteDataBuffer(PATTERN).reader(ByteOrder.BIG_ENDIAN);
            BaseJaveTypesBufferReader primitiveReader = new BaseJaveTypesBufferReader(reader);

            assertEquals(0x01234567, primitiveReader.getInt());

            assertEquals(Integer.BYTES, reader.offset());
        }

        @Test
        void getUnsignedIntAdvancesByFourBytes() {
            ByteDataBuffer.ByteBufferReader reader = new ByteDataBuffer(new byte[]{
                    (byte) 0xFF,
                    (byte) 0xFF,
                    (byte) 0xFF,
                    (byte) 0xFF
            }).reader(ByteOrder.BIG_ENDIAN);
            BaseJaveTypesBufferReader primitiveReader = new BaseJaveTypesBufferReader(reader);

            assertEquals(4_294_967_295L, primitiveReader.getUnsignedInt());

            assertEquals(Integer.BYTES, reader.offset());
        }

        @Test
        void getLongAdvancesByEightBytes() {
            ByteDataBuffer.ByteBufferReader reader = new ByteDataBuffer(PATTERN).reader(ByteOrder.BIG_ENDIAN);
            BaseJaveTypesBufferReader primitiveReader = new BaseJaveTypesBufferReader(reader);

            assertEquals(0x0123456789ABCDEFL, primitiveReader.getLong());

            assertEquals(Long.BYTES, reader.offset());
        }

        @Test
        void getUnsignedLongAdvancesByEightBytes() {
            ByteDataBuffer.ByteBufferReader reader = new ByteDataBuffer(
                    new byte[]{
                            (byte) 0xFF,
                            (byte) 0xFF,
                            (byte) 0xFF,
                            (byte) 0xFF,
                            (byte) 0xFF,
                            (byte) 0xFF,
                            (byte) 0xFF,
                            (byte) 0xFF
                    }
            ).reader(ByteOrder.BIG_ENDIAN);
            BaseJaveTypesBufferReader primitiveReader = new BaseJaveTypesBufferReader(reader);

            assertEquals(new BigInteger("18446744073709551615"), primitiveReader.getUnsignedLong());

            assertEquals(Long.BYTES, reader.offset());
            assertFalse(reader.hasRemaining());
        }

        @Test
        void getFloatAdvancesByFourBytes() {
            float expected = 12.25f;
            byte[] bytes = ByteBuffer.allocate(Float.BYTES)
                    .order(ByteOrder.BIG_ENDIAN)
                    .putFloat(expected)
                    .array();
            ByteDataBuffer.ByteBufferReader reader = new ByteDataBuffer(bytes).reader(ByteOrder.BIG_ENDIAN);
            BaseJaveTypesBufferReader primitiveReader = new BaseJaveTypesBufferReader(reader);

            assertEquals(expected, primitiveReader.getFloat());

            assertEquals(Float.BYTES, reader.offset());
        }

        @Test
        void getDoubleAdvancesByEightBytes() {
            double expected = 12.25d;
            byte[] bytes = ByteBuffer.allocate(Double.BYTES)
                    .order(ByteOrder.BIG_ENDIAN)
                    .putDouble(expected)
                    .array();
            ByteDataBuffer.ByteBufferReader reader = new ByteDataBuffer(bytes).reader(ByteOrder.BIG_ENDIAN);
            BaseJaveTypesBufferReader primitiveReader = new BaseJaveTypesBufferReader(reader);

            assertEquals(expected, primitiveReader.getDouble());

            assertEquals(Double.BYTES, reader.offset());
        }

        @Test
        void getStringAdvancesByReadLength() {
            byte[] bytes = "hello-world".getBytes(StandardCharsets.UTF_8);
            ByteDataBuffer.ByteBufferReader reader = new ByteDataBuffer(bytes).reader(ByteOrder.BIG_ENDIAN);
            BaseJaveTypesBufferReader primitiveReader = new BaseJaveTypesBufferReader(reader);

            assertEquals("hello", primitiveReader.getString(5, StandardCharsets.UTF_8));

            assertEquals(5, reader.offset());
            assertEquals("-world", primitiveReader.getString(6, StandardCharsets.UTF_8));
            assertFalse(reader.hasRemaining());
        }


        @Test
        void sequentialGetMethodsReadFromUpdatedOffset() {
            byte[] bytes = ByteBuffer.allocate(Short.BYTES + Integer.BYTES + Long.BYTES + Float.BYTES + Double.BYTES)
                    .order(ByteOrder.BIG_ENDIAN)
                    .putShort((short) 0x1234)
                    .putInt(0x456789AB)
                    .putLong(0xCDEF1032547698BAL)
                    .putFloat(-9.5f)
                    .putDouble(Math.PI)
                    .array();
            ByteDataBuffer.ByteBufferReader reader = new ByteDataBuffer(bytes).reader(ByteOrder.BIG_ENDIAN);
            BaseJaveTypesBufferReader primitiveReader = new BaseJaveTypesBufferReader(reader);

            assertEquals((short) 0x1234, primitiveReader.getShort());
            assertEquals(0x456789AB, primitiveReader.getInt());
            assertEquals(0xCDEF1032547698BAL, primitiveReader.getLong());
            assertEquals(-9.5f, primitiveReader.getFloat());
            assertEquals(Math.PI, primitiveReader.getDouble());

            assertEquals(bytes.length, reader.offset());
            assertFalse(reader.hasRemaining());
        }

        @Test
        void getMethodsCanReadExactlyToEndOfBuffer() {
            ByteDataBuffer.ByteBufferReader shortReader = new ByteDataBuffer(new byte[]{0x12, 0x34}).reader(ByteOrder.BIG_ENDIAN);
            ByteDataBuffer.ByteBufferReader intReader = new ByteDataBuffer(new byte[]{0x12, 0x34, 0x56, 0x78}).reader(ByteOrder.BIG_ENDIAN);
            ByteDataBuffer.ByteBufferReader longReader = new ByteDataBuffer(PATTERN).reader(ByteOrder.BIG_ENDIAN);

            assertEquals((short) 0x1234, new BaseJaveTypesBufferReader(shortReader).getShort());
            assertEquals(0x12345678, new BaseJaveTypesBufferReader(intReader).getInt());
            assertEquals(0x0123456789ABCDEFL, new BaseJaveTypesBufferReader(longReader).getLong());

            assertFalse(shortReader.hasRemaining());
            assertFalse(intReader.hasRemaining());
            assertEquals(PATTERN.length - Long.BYTES, longReader.remaining());
        }
    }

    @Nested
    class WrappedReaderFailures {

        @Test
        void peekMethodsRejectNegativeRelativeOffsets() {
            BaseJaveTypesBufferReader primitiveReader = new BaseJaveTypesBufferReader(
                    new ByteDataBuffer(new byte[Long.BYTES]).reader(ByteOrder.BIG_ENDIAN)
            );

            assertThrows(IndexOutOfBoundsException.class, () -> primitiveReader.peekShort(-1));
            assertThrows(IndexOutOfBoundsException.class, () -> primitiveReader.peekUnsignedShort(-1));
            assertThrows(IndexOutOfBoundsException.class, () -> primitiveReader.peekInt(-1));
            assertThrows(IndexOutOfBoundsException.class, () -> primitiveReader.peekUnsignedInt(-1));
            assertThrows(IndexOutOfBoundsException.class, () -> primitiveReader.peekLong(-1));
            assertThrows(IndexOutOfBoundsException.class, () -> primitiveReader.peekUnsignedLong(-1));
            assertThrows(IndexOutOfBoundsException.class, () -> primitiveReader.peekFloat(-1));
            assertThrows(IndexOutOfBoundsException.class, () -> primitiveReader.peekDouble(-1));
            assertThrows(IndexOutOfBoundsException.class, () -> primitiveReader.peekString(-1, 1));
        }

        @Test
        void peekMethodsRejectInsufficientBytesAtCurrentOffset() {
            BaseJaveTypesBufferReader primitiveReader = new BaseJaveTypesBufferReader(
                    new ByteDataBuffer(new byte[]{0x01}).reader(ByteOrder.BIG_ENDIAN)
            );

            assertThrows(IndexOutOfBoundsException.class, primitiveReader::peekShort);
            assertThrows(IndexOutOfBoundsException.class, primitiveReader::peekUnsignedShort);
            assertThrows(IndexOutOfBoundsException.class, primitiveReader::peekInt);
            assertThrows(IndexOutOfBoundsException.class, primitiveReader::peekUnsignedInt);
            assertThrows(IndexOutOfBoundsException.class, primitiveReader::peekLong);
            assertThrows(IndexOutOfBoundsException.class, primitiveReader::peekUnsignedLong);
            assertThrows(IndexOutOfBoundsException.class, primitiveReader::peekFloat);
            assertThrows(IndexOutOfBoundsException.class, primitiveReader::peekDouble);
        }

        @Test
        void peekMethodsRejectInsufficientBytesAtRelativeOffset() {
            BaseJaveTypesBufferReader primitiveReader = new BaseJaveTypesBufferReader(
                    new ByteDataBuffer(new byte[Long.BYTES]).reader(ByteOrder.BIG_ENDIAN)
            );

            assertThrows(IndexOutOfBoundsException.class, () -> primitiveReader.peekShort(Long.BYTES - 1));
            assertThrows(IndexOutOfBoundsException.class, () -> primitiveReader.peekUnsignedShort(Long.BYTES - 1));
            assertThrows(IndexOutOfBoundsException.class, () -> primitiveReader.peekInt(Long.BYTES - 3));
            assertThrows(IndexOutOfBoundsException.class, () -> primitiveReader.peekUnsignedInt(Long.BYTES - 3));
            assertThrows(IndexOutOfBoundsException.class, () -> primitiveReader.peekLong(1));
            assertThrows(IndexOutOfBoundsException.class, () -> primitiveReader.peekUnsignedLong(1));
            assertThrows(IndexOutOfBoundsException.class, () -> primitiveReader.peekFloat(Long.BYTES - 3));
            assertThrows(IndexOutOfBoundsException.class, () -> primitiveReader.peekDouble(1));
        }

        @Test
        void getMethodsDoNotAdvanceWhenReadFails() {
            ByteDataBuffer.ByteBufferReader reader = new ByteDataBuffer(new byte[]{0x01}).reader(ByteOrder.BIG_ENDIAN);
            BaseJaveTypesBufferReader primitiveReader = new BaseJaveTypesBufferReader(reader);

            assertThrows(IndexOutOfBoundsException.class, primitiveReader::getShort);
            assertThrows(IndexOutOfBoundsException.class, primitiveReader::getUnsignedShort);
            assertThrows(IndexOutOfBoundsException.class, primitiveReader::getInt);
            assertThrows(IndexOutOfBoundsException.class, primitiveReader::getUnsignedInt);
            assertThrows(IndexOutOfBoundsException.class, primitiveReader::getLong);
            assertThrows(IndexOutOfBoundsException.class, primitiveReader::getUnsignedLong);
            assertThrows(IndexOutOfBoundsException.class, primitiveReader::getFloat);
            assertThrows(IndexOutOfBoundsException.class, primitiveReader::getDouble);

            assertEquals(0, reader.offset());
        }

        @Test
        void failureMessageContainsRequestedLengthRelativeOffsetCurrentOffsetAndRemainingBytes() {
            ByteDataBuffer.ByteBufferReader reader = new ByteDataBuffer(new byte[]{0x01, 0x02}).reader(ByteOrder.BIG_ENDIAN);
            reader.skip(1);
            BaseJaveTypesBufferReader primitiveReader = new BaseJaveTypesBufferReader(reader);

            IndexOutOfBoundsException exception = assertThrows(
                    IndexOutOfBoundsException.class,
                    () -> primitiveReader.peekInt(1)
            );

            assertAll(
                    () -> assertTrue(exception.getMessage().contains("Requested 4 byte(s)")),
                    () -> assertTrue(exception.getMessage().contains("relative offset 1")),
                    () -> assertTrue(exception.getMessage().contains("current offset is 1")),
                    () -> assertTrue(exception.getMessage().contains("remaining bytes are 1"))
            );
        }
    }

    private static byte[] withPadding(byte[] payload) {
        byte[] result = new byte[payload.length + 4];
        result[0] = 0x55;
        result[1] = 0x66;
        System.arraycopy(payload, 0, result, 2, payload.length);
        result[result.length - 2] = 0x77;
        result[result.length - 1] = 0x11;
        return result;
    }
}