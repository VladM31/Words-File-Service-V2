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

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
class WavUploadService implements UploadService {
    private static final Set<String> SUPPORTED_EXTENSIONS = Set.of("wav");
    private static final int VBR_QUALITY = 2; // VBR quality (0-9, lower better), 2 is ~190 kbps, good balance with minimal loss

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

        var sourcePath = Files.createTempFile(tempDirectory, "wav-upload-", ".wav");
        try (AutoCloseable ignored = () -> Files.deleteIfExists(sourcePath);
             InputStream in = file.getInputStream()) {
            Files.copy(in, sourcePath, StandardCopyOption.REPLACE_EXISTING);

            MultimediaObject multimediaObject = new MultimediaObject(sourcePath.toFile());
            MultimediaInfo info = multimediaObject.getInfo();

            if (info.getAudio() == null) {
                throw new IOException("Invalid audio file.");
            }

            Encoder encoder = new Encoder();

            String newFileName = user.id() + "_" + fileNameGenerator.generate() + ".mp3";
            Path outputPath = directory.resolve(newFileName);
            Files.createDirectories(outputPath.getParent());


            EncodingAttributes attr = AudioUtils.toEncodingAttributes(info, VBR_QUALITY);

            encoder.encode(multimediaObject, outputPath.toFile(), attr);

            return new UploadResult(newFileName);
        } catch (Exception e) {
            log.error("Error during WAV to MP3 conversion", e);
            throw new IOException("Failed to convert and upload WAV file to MP3", e);
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