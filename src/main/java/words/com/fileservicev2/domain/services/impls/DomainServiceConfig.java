package words.com.fileservicev2.domain.services.impls;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import words.com.fileservicev2.domain.services.FileNameGenerator;
import words.com.fileservicev2.domain.services.ImageContentAnalyzer;
import words.com.fileservicev2.domain.services.UploadService;
import words.com.fileservicev2.domain.utils.AppUtils;


import java.nio.file.Path;
import java.util.List;
import java.util.Random;

public class DomainServiceConfig {

    @Bean
    FileNameGenerator uuidFileNameGenerator() {
        return new UuidFileNameGenerator(new Random());
    }

    @Bean
    UploadService imageUploadService(
            @Value("${server.image.upload.max-dimension}")
            int imageUploadMaxDimension,
            FileNameGenerator fileNameGenerator,
            @Value("${server.image.upload.directory}")
            String directoryPath,
            List<ImageContentAnalyzer> imageContentAnalyzers
    ) {
        Path directory = Path.of(AppUtils.getFilePrefixByOs() + directoryPath);
        return new ImageUploadService(imageUploadMaxDimension, fileNameGenerator, directory,imageContentAnalyzers);
    }

    @Bean
    UploadService gifUploadService(
            @Value("${server.image.upload.max-dimension}")
            int imageUploadMaxDimension,
            FileNameGenerator fileNameGenerator,
            @Value("${server.image.upload.directory}")
            String directoryPath,
            List<ImageContentAnalyzer> imageContentAnalyzers
    ) {
        Path directory = Path.of(AppUtils.getFilePrefixByOs() + directoryPath);
        return new GifUploadService(imageUploadMaxDimension, fileNameGenerator, directory,imageContentAnalyzers);
    }

    @Bean
    UploadService mp3UploadService(
            FileNameGenerator fileNameGenerator,
            @Value("${server.audio.upload.directory}")
            String directoryPath,
            @Value("${server.temp-audio.upload.directory}")
            String tempDirectoryPath
    ) {
        Path directory = Path.of(AppUtils.getFilePrefixByOs() + directoryPath);
        Path tempDirectory = Path.of(AppUtils.getFilePrefixByOs() + tempDirectoryPath);
        return new Mp3UploadService(fileNameGenerator, directory, tempDirectory);
    }

    @Bean
    ImageContentAnalyzer oujingzhouImageContentAnalyzer(
            @Value("${server.oujingzhou.upload.directory}")
            String directoryPath
    ) {
        Path directory = Path.of(AppUtils.getFilePrefixByOs() + directoryPath);
        return new OujingzhouImageContentAnalyzer(directory,0.45f);
    }

    @Bean
    UploadService walUploadService(
            FileNameGenerator fileNameGenerator,
            @Value("${server.audio.upload.directory}")
            String directoryPath,
            @Value("${server.temp-audio.upload.directory}")
            String tempDirectoryPath
    ) {
        Path directory = Path.of(AppUtils.getFilePrefixByOs() + directoryPath);
        Path tempDirectory = Path.of(AppUtils.getFilePrefixByOs() + tempDirectoryPath);
        return new WavUploadService(fileNameGenerator, directory, tempDirectory);
    }
}
