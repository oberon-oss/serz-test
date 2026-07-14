package eu.oberon.oss.tools.tsc.serz;

import eu.oberon.oss.tools.tsc.serz.io.SERZBinaryFileTypeDetector;
import eu.oberon.oss.tools.tsc.serz.parse.BinarySERZParser;
import eu.oberon.oss.tools.tsc.serz.util.ByteDataBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "serz",
        description = "Tool to analyze SERZ files",
        version = "1.0.0"
)
public class Command implements Callable<Integer> {
    private static final Logger LOGGER = LoggerFactory.getLogger(Command.class);

    @CommandLine.Option(
            names = {"-a", "--action"},
            description = "Action to perform",
            required = true
    )
    private String action;

    @CommandLine.Option(
            names = {"-p", "--path"},
            description = "File or directory to process",
            required = true
    )
    private Path path;

    static void main(String[] args) {
        System.exit(new CommandLine(new Command()).execute(args));
    }

    @Override
    public Integer call() throws Exception {
        switch (action) {
            case "basic-file-scan":
                byte[] data = loadBinaryData(path);
                ByteDataBuffer byteDataBuffer = new ByteDataBuffer(data);
                new BinarySERZParser(byteDataBuffer).process();
                return 0;
            default:
                LOGGER.error("Unknown action: {}", action);
                return -1;
        }
    }

    private static byte[] loadBinaryData(Path path) throws IOException {
        String type = Files.probeContentType(path);
        if (!SERZBinaryFileTypeDetector.MIME_TYPE.contentEquals(type)) {
            throw new IllegalArgumentException("Not a binary SERZ file: " + path);
        }

        try (InputStream inputStream = Files.newInputStream(path)) {
            return inputStream.readAllBytes();
        }
    }
}
