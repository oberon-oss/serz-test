package eu.oberon.oss.tools.tsc.serz.io;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.spi.FileTypeDetector;

/**
 * A custom {@link FileTypeDetector} implementation that detects the MIME type of XML files specific to the SERZ format.
 * <p>
 * The primary purpose of this class is to determine whether an XML file belongs to the SERZ format based on its content, specifically by checking if the file
 * contains the "<a href="http://www.kuju.com/TnT/2003/Delta">...</a>" namespace in its XML structure. If the namespace is detected, the file's MIME type is
 * identified as "application/x-tsc-serz+xml".
 * <p>
 * Key Features:
 * <ul>
 *   <li>The detection process reads the XML content of the file and verifies the presence of the specific namespace, either in the root
 *   element, its attributes, or declared namespaces.</li>
 *   <li>Ensures safe processing by disabling support for DTDs and external entities during XML parsing.</li>
 * </ul>
 * <p>
 * Notes:
 * <ul>
 *   <li>Files that are not regular files, do not have an ".xml" extension, or encounter parsing issues will not be detected as the
 *   SERZ XML type.</li>
 *   <li>This implementation relies on an {@link XMLStreamReader} for processing the XML content efficiently.</li>
 * </ul>
 */
public final class SERZXmlFileTypeDetector extends FileTypeDetector {
    /**
     * The MIME type for SERZ XML files.
     *
     * @since 1.0.0
     */
    public static final String MIME_TYPE = "application/x-tsc-serz+xml";

    /**
     * The namespace URI for the SERZ XML format.
     *
     * @since 1.0.0
     */
    @SuppressWarnings("HttpUrlsUsage") // We are only looking for a specific namespace URI in a document.
    public static final String KUJU_NS = "http://www.kuju.com/TnT/2003/Delta";

    /**
     * Constructs a new instance of {@link SERZXmlFileTypeDetector}.
     *
     * @since 1.0.0
     */
    public SERZXmlFileTypeDetector() {
        // Keep Javadoc happy
    }

    @Override
    public String probeContentType(Path path) throws IOException {
        if (!Files.isRegularFile(path)) {
            return null;
        }
        try (InputStream inputStream = Files.newInputStream(path)) {
            return isTscXml(inputStream) ? MIME_TYPE : null;
        } catch (XMLStreamException _) {
            return null;
        }
    }

    private static boolean isTscXml(InputStream inputStream) throws XMLStreamException {
        XMLInputFactory factory = XMLInputFactory.newFactory();
        factory.setProperty(XMLInputFactory.IS_COALESCING, true);
        factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
        factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);

        XMLStreamReader reader = factory.createXMLStreamReader(inputStream);
        try {
            while (reader.hasNext()) {
                int event = reader.next();

                if (event == XMLStreamConstants.START_ELEMENT) {
                    return hasKujuNamespace(reader);
                }
            }

            return false;
        } finally {
            reader.close();
        }
    }

    private static boolean hasKujuNamespace(XMLStreamReader reader) {
        String elementNamespace = reader.getNamespaceURI();
        if (KUJU_NS.equals(elementNamespace)) {
            return true;
        }

        for (int i = 0; i < reader.getNamespaceCount(); i++) {
            if (KUJU_NS.equals(reader.getNamespaceURI(i))) {
                return true;
            }
        }

        for (int i = 0; i < reader.getAttributeCount(); i++) {
            if (KUJU_NS.equals(reader.getAttributeNamespace(i))) {
                return true;
            }
        }

        return false;
    }
}