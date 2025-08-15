package words.com.fileservicev2.domain.services.impls;

import com.madgag.gif.fmsware.AnimatedGifEncoder;
import com.madgag.gif.fmsware.GifDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import words.backend.authmodule.net.models.User;
import words.com.fileservicev2.domain.exceptions.GifUploadException;
import words.com.fileservicev2.domain.exceptions.ImageUploadException;
import words.com.fileservicev2.domain.models.UploadResult;
import words.com.fileservicev2.domain.models.enums.FileDirectory;
import words.com.fileservicev2.domain.services.FileNameGenerator;
import words.com.fileservicev2.domain.services.ImageContentAnalyzer;
import words.com.fileservicev2.domain.services.UploadService;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import static words.com.fileservicev2.domain.utils.ImageUtils.resizeImage;

@Slf4j
@RequiredArgsConstructor
class GifUploadService implements UploadService {
    private static final String SUPPORTED_EXTENSION = "gif";
    private static final Set<String> SUPPORTED_EXTENSIONS = Set.of(SUPPORTED_EXTENSION);

    private final int imageUploadMaxDimension;
    private final FileNameGenerator fileNameGenerator;
    private final Path directory;
    private final List<ImageContentAnalyzer> imageContentAnalyzers;

    @Override
    public boolean canUpload(String fileName) {
        return fileName.endsWith(SUPPORTED_EXTENSION);
    }

    @Override
    public UploadResult upload(MultipartFile file, User user) throws IOException {
        var inputStream = file.getInputStream();
        var content = inputStream.readAllBytes();

        for (ImageContentAnalyzer imageContentAnalyzer : imageContentAnalyzers) {
            if (imageContentAnalyzer.isUnValid(content, SUPPORTED_EXTENSION)) {
                log.warn("Image content is invalid according to analyzer: {}", imageContentAnalyzer.getName());
                throw new ImageUploadException("Image content is invalid.");
            }
        }

        var newFileName = user.id() + "_" +  fileNameGenerator.generate() + "." + SUPPORTED_EXTENSION;
        Path outputPath = directory.resolve(newFileName);
        if (!Files.exists(outputPath.getParent())) {
            Files.createDirectories(outputPath.getParent());
        }
        Files.createFile(outputPath);

        processAnimatedGif(new ByteArrayInputStream(content), outputPath);

        return new UploadResult(newFileName);
    }

    private void processAnimatedGif(InputStream inputStream, Path outputPath)  {
        GifDecoder decoder = new GifDecoder();
        int status = decoder.read(inputStream);
        if (status != GifDecoder.STATUS_OK) {
            throw new GifUploadException("Failed to read GIF image");
        }

        AnimatedGifEncoder encoder = new AnimatedGifEncoder();
        try(FileOutputStream fos = new FileOutputStream(outputPath.toFile());
            AutoCloseable encoderCloseable = encoder::finish) {
            encoder.start(fos);
            encoder.setRepeat(decoder.getLoopCount());

            int frameCount = decoder.getFrameCount();

            for (int i = 0; i < frameCount; i++) {

                BufferedImage frame = decoder.getFrame(i);
                int delay = decoder.getDelay(i);

                // Изменяем размер кадра
                BufferedImage resizedFrame = resizeImage(frame, imageUploadMaxDimension , null);

                encoder.setDelay(delay);
                encoder.addFrame(resizedFrame);
            }
        } catch (Exception e) {
            throw new GifUploadException("Error while work with gif",e);
        }
    }



    @Override
    public Set<String> getSupportedExtensions() {
        return SUPPORTED_EXTENSIONS;
    }

    @Override
    public FileDirectory getFileDirectory() {
        return FileDirectory.IMAGE;
    }
}
