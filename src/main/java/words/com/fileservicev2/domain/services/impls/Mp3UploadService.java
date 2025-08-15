package words.com.fileservicev2.domain.services.impls;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import words.backend.authmodule.net.models.User;
import words.com.fileservicev2.domain.models.UploadResult;
import words.com.fileservicev2.domain.models.enums.FileDirectory;
import words.com.fileservicev2.domain.services.FileNameGenerator;
import words.com.fileservicev2.domain.services.UploadService;
import words.com.fileservicev2.domain.utils.AudioUtils;
import ws.schild.jave.Encoder;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.encode.EncodingAttributes;
import ws.schild.jave.info.MultimediaInfo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
class Mp3UploadService implements UploadService {
    private static final Set<String> SUPPORTED_EXTENSIONS = Set.of("mp3");
    private static final int TARGET_BITRATE_KBPS = 160;
    private static final int VBR_QUALITY = 4; // Co

    private final FileNameGenerator fileNameGenerator;
    private final Path directory;
    private final Path tempDirectory;

    @Override
    public boolean canUpload(String fileName) {
        return SUPPORTED_EXTENSIONS.stream().anyMatch(ext -> fileName.toLowerCase().endsWith("." + ext));
    }

    @Override
    public UploadResult upload(MultipartFile file, User user) throws IOException {
        if (!Files.exists(tempDirectory)) {
            Files.createDirectories(tempDirectory);
        }

        var sourcePath = Files.createTempFile(tempDirectory, "mp3-upload-", ".tmp");
        var source = sourcePath.toFile();
        try (AutoCloseable ignored = () -> Files.deleteIfExists(sourcePath);
             InputStream in = file.getInputStream()) {
            Files.copy(in, sourcePath, StandardCopyOption.REPLACE_EXISTING);

            MultimediaObject multimediaObject = new MultimediaObject(source);
            MultimediaInfo info = multimediaObject.getInfo();

            if (info.getAudio() == null) {
                throw new IOException("Invalid audio file.");
            }

            // Calculate exact average bitrate from file size and duration
            long durationMs = info.getDuration();
            if (durationMs <= 0) {
                durationMs = 1; // Avoid division by zero
            }
            double originalBitRateKbps = (Files.size(sourcePath) * 8.0 / (durationMs / 1000.0)) / 1000.0;

            String newFileName = user.id() + "_" + fileNameGenerator.generate() + ".mp3";
            Path outputPath = directory.resolve(newFileName);
            Files.createDirectories(outputPath.getParent());

            File target = outputPath.toFile();

            if (originalBitRateKbps <= TARGET_BITRATE_KBPS) {
                // No need to compress, just copy to avoid additional loss
                Files.copy(sourcePath, outputPath);
                log.info("File copied without re-encoding, original bitrate: {} kbps", originalBitRateKbps);
                return new UploadResult(newFileName);
            }
            // Compress with VBR quality
            Encoder encoder = new Encoder();

            EncodingAttributes attr = AudioUtils.toEncodingAttributes(info, VBR_QUALITY);

            encoder.encode(multimediaObject, target, attr);
            log.info("File compressed, original bitrate: {} kbps, target quality: {}", originalBitRateKbps, VBR_QUALITY);

            return new UploadResult(newFileName);
        } catch (Exception e) {
            log.error("Error during MP3 encoding", e);
            throw new RuntimeException("Failed to compress and upload MP3 file", e);
        }
    }

    @Override
    public Set<String> getSupportedExtensions() {
        return SUPPORTED_EXTENSIONS;
    }

    @Override
    public FileDirectory getFileDirectory() {
        return FileDirectory.AUDIO;
    }
}