package eu.oberon.oss.tools.tsc.serz.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * Provides functionality to format a byte array into a readable hexadecimal and character representation. The formatted output includes a header, with
 * hexadecimal offsets, the hexadecimal representation of the bytes, and an ASCII character representation for printable characters. Non-printable characters
 * are replaced with a dot ('.').
 * <p>
 * This class is not instantiable.
 *
 * @author TigerLilly64
 * @since 1.0.0
 */
public class SimpleByteFormatter {
    private SimpleByteFormatter() {
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleByteFormatter.class);

    private static final String HEX_HEADER = "00 01 02 03 04 05 06 07 08 09 0A 0B 0C 0D 0E 0F";
    private static final String FILLER = " --- ";
    private static final String CHAR_HEADER = "0123456789ABCDEF";

    /**
     * Formats a byte array into a readable hexadecimal and character representation.
     *
     * @param binaryData The byte array to format.
     *
     * @return The formatted string representation of the byte array.
     *
     * @since 1.0.0
     */
    public static String formatBytes(final byte[] binaryData) {
        StringBuilder work = new StringBuilder(createHeader()).append("\n");

        if (binaryData == null || binaryData.length == 0) {
            return "***** No data was specified *****";
        }

        int bytesRemaining = binaryData.length;

        for (int i = 0; i < binaryData.length; i += 16, bytesRemaining -= 16) {
            LOGGER.debug("bytes remaining= {})", bytesRemaining);
            work.append(String.format("%08X  ", i));
            work.append(format16ByteSegment(Arrays.copyOfRange(binaryData, i, bytesRemaining < 16 ? i + bytesRemaining : i + 16))).append("\n");
        }
        return work.append("\n").toString();
    }

    private static String format16ByteSegment(byte[] segment) {
        LOGGER.debug("segment size= {})", segment.length);

        StringBuilder hexData = new StringBuilder();
        StringBuilder charData = new StringBuilder("[");
        for (int i = 0; i < segment.length; i++) {
            hexData.append(String.format("%02X", segment[i])).append(i < segment.length - 1 ? " " : "");
            if (segment[i] >= 32 && segment[i] <= 126) {
                charData.append((char) segment[i]);
            } else {
                charData.append(".");
            }
        }
        if (segment.length < 16) {
            hexData.repeat(" ", 3 * (16 - segment.length));
            charData.repeat(" ", 16 - segment.length);
        }
        return hexData.repeat(" ", FILLER.length()).append(charData.append("]")).toString();
    }

    private static String createHeader() {
        return " ".repeat(10) +
                HEX_HEADER +
                FILLER +
                " " + CHAR_HEADER +
                "\n" +
                " ".repeat(10) +
                "-".repeat(HEX_HEADER.length()) +
                " ".repeat(FILLER.length()) +
                " " +
                "-".repeat(CHAR_HEADER.length())
                ;
    }

}
