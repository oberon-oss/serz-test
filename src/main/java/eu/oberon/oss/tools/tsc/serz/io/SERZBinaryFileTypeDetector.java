package eu.oberon.oss.tools.tsc.serz.io;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.spi.FileTypeDetector;
import java.util.Arrays;

/**
 * A custom {@link FileTypeDetector} implementation that detects the MIME type for binary SERZ files.
 * <p>
 * This implementation checks for a specific magic header in the first 8 bytes of the file to determine if the file corresponds to the SERZ binary format. If
 * the file matches the header, it returns the MIME type "application/x-serz".
 * <p>
 * The detection process is achieved through two methods: 1. {@link #probeContentType(Path)}: Reads the first 8 bytes from a file and checks if it matches the
 * expected SERZ magic header. 2. {@link #probeContentType(byte[])}: Accepts a raw byte array and determines if the provided byte array matches the SERZ magic
 * header.
 * <p>
 * This detector only applies to regular files, returning {@code null} for other file types or structures that do not match the SERZ magic header.
 * <p>
 * Note: Detection is based on comparing the file's initial bytes with a predefined magic header. It is essential to ensure that the file is readable and has
 * sufficient content for the format detection.
 *
 * @author TigerLilly64
 * @since 1.0.0
 */
public final class SERZBinaryFileTypeDetector extends FileTypeDetector {
    /**
     * The default constructor for SERZBinaryFileTypeDetector.
     *
     * @since 1.0.0
     */
    public SERZBinaryFileTypeDetector() {
        // Keep Javadoc happy
    }

    /**
     * The MIME type associated with SERZ binary files.
     *
     * @since 1.0.0
     */
    public static final String MIME_TYPE = "application/x-serz";

    private static final byte[] SERZ_MAGIC = {'S', 'E', 'R', 'Z', 0, 0, 1, 0};

    /**
     * Probes the given file path to determine if it is a SERZ binary file.
     *
     * @param path the path to the file to probe
     * @return the MIME type {@code "application/x-serz"} if the file matches the SERZ magic header, or {@code null} otherwise
     * @throws IOException if an I/O error occurs reading the file
     * @since 1.0.0
     */
    @Override
    public String probeContentType(Path path) throws IOException {
        if (!Files.isRegularFile(path)) {
            return null;
        }
        try (InputStream inputStream = Files.newInputStream(path)) {
            return probeContentType(inputStream.readNBytes(8));
        }
    }

    /**
     * Probes the provided byte array to determine if it matches the magic header of the SERZ binary format. If the byte array corresponds to the SERZ format,
     * the method returns the associated MIME type; otherwise, {@code null} is returned.
     *
     * @param input the byte array to be analyzed; must be at least 8 bytes long to match the expected header.
     *
     * @return the MIME type {@code "application/x-serz"} if the input matches the SERZ magic header; {@code null} if the input is not a valid SERZ file or does
     *         not match the header.
     *
     * @since 1.0.0
     */
    public static String probeContentType(final byte[] input) {
        if (input.length < SERZ_MAGIC.length) {
            return null;
        }

        if (Arrays.compare(input, 0, 8, SERZ_MAGIC, 0, 8) == 0) {
            return MIME_TYPE;
        }

        return null;
    }
}