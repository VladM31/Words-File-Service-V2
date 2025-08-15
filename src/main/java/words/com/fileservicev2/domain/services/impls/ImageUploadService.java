package words.com.fileservicev2.domain.services.impls;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import words.backend.authmodule.net.models.User;
import words.com.fileservicev2.domain.exceptions.ImageUploadException;
import words.com.fileservicev2.domain.models.UploadResult;
import words.com.fileservicev2.domain.models.enums.FileDirectory;
import words.com.fileservicev2.domain.services.FileNameGenerator;
import words.com.fileservicev2.domain.services.ImageContentAnalyzer;
import words.com.fileservicev2.domain.services.UploadService;
import words.com.fileservicev2.domain.utils.ImageUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import static words.com.fileservicev2.domain.utils.ImageUtils.resizeImage;


@Slf4j
@RequiredArgsConstructor
class ImageUploadService implements UploadService {
    private static final String OUTPUT_FORMAT = ImageUtils.WEBP_FORMAT;
    private static final Set<String> SUPPORTED_EXTENSIONS = Set.of("jpg", "jpeg", "png", OUTPUT_FORMAT);


    private final int imageUploadMaxDimension;
    private final FileNameGenerator fileNameGenerator;
    private final Path directory;
    private final List<ImageContentAnalyzer> imageContentAnalyzers;

    @Override
    public boolean canUpload(String fileName) {
        return SUPPORTED_EXTENSIONS.stream().anyMatch(fileName::endsWith);
    }

    @Override
    public UploadResult upload(MultipartFile file, User user) throws IOException {
        BufferedImage image = ImageIO.read(file.getInputStream());
        if (image == null) {
            throw new ImageUploadException("Invalid image file.");
        }
        BufferedImage resizedImage = resizeImage(image, imageUploadMaxDimension, 0.75);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(resizedImage, OUTPUT_FORMAT, baos);

        for (ImageContentAnalyzer imageContentAnalyzer : imageContentAnalyzers) {
            if (imageContentAnalyzer.isUnValid(baos.toByteArray(), OUTPUT_FORMAT)) {
                log.warn("Image content is invalid according to analyzer: {}", imageContentAnalyzer.getName());
                throw new ImageUploadException("Image content is invalid.");
            }
        }

        var newFileName = user.id() + "_" +  fileNameGenerator.generate() + "." + OUTPUT_FORMAT;
        Path outputPath = directory.resolve(newFileName);
        if (!Files.exists(outputPath.getParent())) {
            Files.createDirectories(outputPath.getParent());
        }
        Files.createFile(outputPath);

        ImageIO.write(resizedImage, OUTPUT_FORMAT, outputPath.toFile());

        return new UploadResult(newFileName);
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
