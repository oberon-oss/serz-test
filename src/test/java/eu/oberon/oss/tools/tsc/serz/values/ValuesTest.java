package eu.oberon.oss.tools.tsc.serz.values;

import eu.oberon.oss.tools.tsc.serz.DataType;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("java:S5778")
class ValuesTest {

    @Test
    void boolValueReturnsCorrectType() {
        assertEquals(DataType.BOOL, new BoolValue(true).type());
        assertEquals(DataType.BOOL, new BoolValue(false).type());
    }

    @Test
    void boolValueCanBeCreatedFromBytes() {
        assertTrue(new BoolValue(new byte[]{0x01}).value());
        assertFalse(new BoolValue(new byte[]{0x00}).value());
        assertFalse(new BoolValue(new byte[]{0x02}).value());
    }

    @Test
    void boolValueCanBeSerializedToBytes() {
        assertArrayEquals(new byte[]{0x01}, new BoolValue(false).toBytes(true));
        assertArrayEquals(new byte[]{0x00}, new BoolValue(true).toBytes(false));
        assertArrayEquals(new byte[]{0x00}, new BoolValue(true).toBytes(null));
    }

    @Test
    void cDeltaStringValueReturnsCorrectType() {
        assertEquals(DataType.C_DELTA_STRING, new CDeltaStringValue("abc").type());
    }

    @Test
    void cDeltaStringValueCanBeCreatedFromBytes() {
        byte[] bytes = "Hello äöü".getBytes(StandardCharsets.UTF_8);

        CDeltaStringValue value = new CDeltaStringValue(bytes);

        assertEquals("Hello äöü", value.value());
    }

    @Test
    void cDeltaStringValueCanBeCreatedFromBytesWithCommonCharsets() {
        String source = "Hello äöü";
        Charset[] charsets = new Charset[]{
                StandardCharsets.UTF_8,
                StandardCharsets.UTF_16,
                StandardCharsets.UTF_16BE,
                StandardCharsets.UTF_16LE,
                StandardCharsets.ISO_8859_1
        };

        for (Charset charset : charsets) {
            byte[] bytes = source.getBytes(charset);

            CDeltaStringValue value = new CDeltaStringValue(bytes, charset);

            assertEquals(source, value.value(), "Decoded value should match for " + charset);
            assertEquals(charset, value.charset(), "Charset should be retained for " + charset);
        }
    }

    @Test
    void cDeltaStringValueCreatedFromBytesWithCharsetSerializesUsingSameCharset() {
        String source = "Hello äöü";
        Charset[] charsets = new Charset[]{
                StandardCharsets.UTF_8,
                StandardCharsets.UTF_16,
                StandardCharsets.UTF_16BE,
                StandardCharsets.UTF_16LE,
                StandardCharsets.ISO_8859_1
        };

        for (Charset charset : charsets) {
            byte[] bytes = source.getBytes(charset);

            CDeltaStringValue value = new CDeltaStringValue(bytes, charset);

            assertArrayEquals(bytes, value.toBytes(source), "Serialized bytes should match for " + charset);
        }
    }

    @Test
    void cDeltaStringValueConstructorWithCharsetRejectsNullBytes() {
        assertThrows(NullPointerException.class, () -> new CDeltaStringValue((byte[]) null, StandardCharsets.UTF_8));
    }

    @Test
    void cDeltaStringValueConstructorWithCharsetRejectsNullCharset() {
        byte[] bytes = "Hello".getBytes(StandardCharsets.UTF_8);

        assertThrows(NullPointerException.class, () -> new CDeltaStringValue(bytes, null));
    }

    @Test
    void cDeltaStringValueCanBeSerializedToBytes() {
        CDeltaStringValue value = new CDeltaStringValue("");

        assertArrayEquals("Hello äöü".getBytes(StandardCharsets.UTF_8), value.toBytes("Hello äöü"));
    }

    @Test
    void sFloat32ValueReturnsCorrectType() {
        assertEquals(DataType.S_FLOAT32, new SFloat32Value(1.5f).type());
    }

    @Test
    void sFloat32ValueCanBeCreatedFromBytes() {
        byte[] bytes = ByteBuffer.allocate(Float.BYTES)
                .order(ByteOrder.LITTLE_ENDIAN)
                .putFloat(123.5f)
                .array();

        SFloat32Value value = new SFloat32Value(bytes);

        assertEquals(123.5f, value.value());
    }

    @Test
    void sFloat32ValueCanBeSerializedToBytes() {
        byte[] expected = ByteBuffer.allocate(Float.BYTES)
                .order(ByteOrder.nativeOrder())
                .putFloat(123.5f)
                .array();

        assertArrayEquals(expected, new SFloat32Value(0.0f).toBytes(123.5f));
    }

    @Test
    void sInt16ValueReturnsCorrectType() {
        assertEquals(DataType.S_INT16, new SInt16Value((short) 42).type());
    }

    @Test
    void sInt16ValueCanBeCreatedFromBytes() {
        byte[] bytes = ByteBuffer.allocate(Short.BYTES)
                .order(ByteOrder.LITTLE_ENDIAN)
                .putShort((short) -1234)
                .array();

        SInt16Value value = new SInt16Value(bytes);

        assertEquals((short) -1234, value.value());
    }

    @Test
    void sInt16ValueCanBeSerializedToBytes() {
        byte[] expected = ByteBuffer.allocate(Short.BYTES)
                .order(ByteOrder.nativeOrder())
                .putShort((short) -1234)
                .array();

        assertArrayEquals(expected, new SInt16Value((short) 0).toBytes((short) -1234));
    }

    @Test
    void sInt32ValueReturnsCorrectType() {
        assertEquals(DataType.S_INT32, new SInt32Value(42).type());
    }

    @Test
    void sInt32ValueCanBeCreatedFromBytes() {
        byte[] bytes = ByteBuffer.allocate(Integer.BYTES)
                .order(ByteOrder.nativeOrder())
                .putInt(-123456)
                .array();

        SInt32Value value = new SInt32Value(bytes);

        assertEquals(-123456, value.value());
    }

    @Test
    void sInt32ValueCanBeSerializedToBytes() {
        byte[] expected = ByteBuffer.allocate(Integer.BYTES)
                .order(ByteOrder.nativeOrder())
                .putInt(-123456)
                .array();

        assertArrayEquals(expected, new SInt32Value(0).toBytes(-123456));
    }

    @Test
    void sUInt8ValueReturnsCorrectType() {
        assertEquals(DataType.S_UINT8, new SUInt8Value((short) 42).type());
    }

    @Test
    void sUInt8ValueAcceptsBoundaryValues() {
        assertEquals((short) 0, new SUInt8Value((short) 0).value());
        assertEquals((short) 255, new SUInt8Value((short) 255).value());
    }

    @Test
    void sUInt8ValueRejectsValuesBelowRange() {
        assertThrows(IllegalArgumentException.class, () -> new SUInt8Value((short) -1));
        assertThrows(IllegalArgumentException.class, () -> new SUInt8Value((short) 0).toBytes((short) -1));
    }

    @Test
    void sUInt8ValueRejectsValuesAboveRange() {
        assertThrows(IllegalArgumentException.class, () -> new SUInt8Value((short) 256));
        assertThrows(IllegalArgumentException.class, () -> new SUInt8Value((short) 0).toBytes((short) 256));
    }

    @Test
    void sUInt8ValueCanBeCreatedFromBytes() {
        assertEquals((short) 255, new SUInt8Value(new byte[]{(byte) 0xFF}).value());
        assertEquals((short) 128, new SUInt8Value(new byte[]{(byte) 0x80}).value());
        assertEquals((short) 1, new SUInt8Value(new byte[]{0x01}).value());
    }

    @Test
    void sUInt8ValueCanBeSerializedToBytes() {
        assertArrayEquals(new byte[]{0x00}, new SUInt8Value((short) 0).toBytes((short) 0));
        assertArrayEquals(new byte[]{0x7F}, new SUInt8Value((short) 0).toBytes((short) 127));
        assertArrayEquals(new byte[]{(byte) 0xFF}, new SUInt8Value((short) 0).toBytes((short) 255));
    }

    @Test
    void sUInt16ValueReturnsCorrectType() {
        assertEquals(DataType.S_UINT16, new SUInt16Value(42).type());
    }

    @Test
    void sUInt16ValueAcceptsBoundaryValues() {
        assertEquals(0, new SUInt16Value(0).value());
        assertEquals(65_535, new SUInt16Value(65_535).value());
    }

    @Test
    void sUInt16ValueRejectsValuesBelowRange() {
        assertThrows(IllegalArgumentException.class, () -> new SUInt16Value(-1));
        assertThrows(IllegalArgumentException.class, () -> new SUInt16Value(0).toBytes(-1));
    }

    @Test
    void sUInt16ValueRejectsValuesAboveRange() {
        assertThrows(IllegalArgumentException.class, () -> new SUInt16Value(65_536));
        assertThrows(IllegalArgumentException.class, () -> new SUInt16Value(0).toBytes(65_536));
    }

    @Test
    void sUInt16ValueCanBeCreatedFromBytes() {
        byte[] bytes = ByteBuffer.allocate(Short.BYTES)
                .putShort((short) 65_535)
                .array();

        SUInt16Value value = new SUInt16Value(bytes);

        assertEquals(65_535, value.value());
    }

    @Test
    void sUInt16ValueCanBeSerializedToBytes() {
        byte[] expected = ByteBuffer.allocate(Short.BYTES)
                .putShort((short) 65_535)
                .array();

        assertArrayEquals(expected, new SUInt16Value(0).toBytes(65_535));
    }

    @Test
    void sUInt32ValueReturnsCorrectType() {
        assertEquals(DataType.S_UINT32, new SUInt32Value(42L).type());
    }

    @Test
    void sUInt32ValueAcceptsBoundaryValues() {
        assertEquals(0L, new SUInt32Value(0L).value());
        assertEquals(4_294_967_295L, new SUInt32Value(4_294_967_295L).value());
    }

    @Test
    void sUInt32ValueRejectsValuesBelowRange() {
        assertThrows(IllegalArgumentException.class, () -> new SUInt32Value(-1L));
        assertThrows(IllegalArgumentException.class, () -> new SUInt32Value(0L).toBytes(-1L));
    }

    @Test
    void sUInt32ValueRejectsValuesAboveRange() {
        assertThrows(IllegalArgumentException.class, () -> new SUInt32Value(4_294_967_296L));
        assertThrows(IllegalArgumentException.class, () -> new SUInt32Value(0L).toBytes(4_294_967_296L));
    }

    @Test
    void sUInt32ValueCanBeCreatedFromBytes() {
        byte[] bytes = ByteBuffer.allocate(Integer.BYTES)
                .putInt(-1)
                .array();

        SUInt32Value value = new SUInt32Value(bytes);

        assertEquals(4_294_967_295L, value.value());
    }

    @Test
    void sUInt32ValueCanBeSerializedToBytes() {
        byte[] expected = ByteBuffer.allocate(Integer.BYTES)
                .putInt(-1)
                .array();

        assertArrayEquals(expected, new SUInt32Value(0L).toBytes(4_294_967_295L));
    }

    @Test
    void sUInt64ValueReturnsCorrectType() {
        assertEquals(DataType.S_UINT64, new SUInt64Value(BigInteger.TEN).type());
    }

    @Test
    void sUInt64ValueAcceptsBoundaryValues() {
        BigInteger max = new BigInteger("18446744073709551615");

        assertEquals(BigInteger.ZERO, new SUInt64Value(BigInteger.ZERO).value());
        assertEquals(max, new SUInt64Value(max).value());
    }

    @Test
    void sUInt64ValueRejectsValuesBelowRange() {
        assertThrows(IllegalArgumentException.class, () -> new SUInt64Value(BigInteger.valueOf(-1)));
        assertThrows(
                IllegalArgumentException.class,
                () -> new SUInt64Value(BigInteger.ZERO).toBytes(BigInteger.valueOf(-1))
        );
    }

    @Test
    void sUInt64ValueRejectsValuesAboveRange() {
        BigInteger tooLarge = new BigInteger("18446744073709551616");

        assertThrows(IllegalArgumentException.class, () -> new SUInt64Value(tooLarge));
        assertThrows(
                IllegalArgumentException.class,
                () -> new SUInt64Value(BigInteger.ZERO).toBytes(tooLarge)
        );
    }

    @Test
    void sUInt64ValueCanBeCreatedFromBytes() {
        byte[] bytes = new byte[]{
                (byte) 0xFF,
                (byte) 0xFF,
                (byte) 0xFF,
                (byte) 0xFF,
                (byte) 0xFF,
                (byte) 0xFF,
                (byte) 0xFF,
                (byte) 0xFF
        };

        SUInt64Value value = new SUInt64Value(bytes);

        assertEquals(new BigInteger("18446744073709551615"), value.value());
    }

    @Test
    void sUInt64ValueCanBeSerializedToBytes() {
        byte[] expected = new byte[]{
                (byte) 0xFF,
                (byte) 0xFF,
                (byte) 0xFF,
                (byte) 0xFF,
                (byte) 0xFF,
                (byte) 0xFF,
                (byte) 0xFF,
                (byte) 0xFF
        };

        assertArrayEquals(
                expected,
                new SUInt64Value(BigInteger.ZERO).toBytes(new BigInteger("18446744073709551615"))
        );
    }

    @Test
    void sUInt64ValueSerializesSmallValuesAsEightBytes() {
        assertArrayEquals(
                new byte[]{0, 0, 0, 0, 0, 0, 0, 1},
                new SUInt64Value(BigInteger.ZERO).toBytes(BigInteger.ONE)
        );
    }
}