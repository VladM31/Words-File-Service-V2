package words.com.fileservicev2.utils;

import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RequiredArgsConstructor
public class FileDeleteCloseable implements AutoCloseable {
    private final Path filePath;

    @Override
    public void close() throws IOException {
        Files.deleteIfExists(filePath);
    }
}
