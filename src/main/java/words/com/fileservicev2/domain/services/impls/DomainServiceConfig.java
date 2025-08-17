package words.com.fileservicev2.domain.services.impls;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import words.com.fileservicev2.db.daos.FileMetadataDao;
import words.com.fileservicev2.domain.mappers.FileDirectionMapper;
import words.com.fileservicev2.domain.mappers.FileMetadataMapper;
import words.com.fileservicev2.domain.services.*;
import words.com.fileservicev2.utils.AppUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
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
        return new ImageUploadService(imageUploadMaxDimension, fileNameGenerator, directory, imageContentAnalyzers);
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
        return new GifUploadService(imageUploadMaxDimension, fileNameGenerator, directory, imageContentAnalyzers);
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
        return new OujingzhouImageContentAnalyzer(directory, 0.45f);
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

    @Bean
    UploadManager uploadManagerImpl(
            List<UploadService> uploadServices,
            FileMetadataDao fileMetadataDao,
            FileMetadataMapper fileMetadataMapper
    ) {
        return new UploadManagerImpl(uploadServices, fileMetadataDao, fileMetadataMapper);
    }

    @Bean
    DownloadService downloadService(
            FileMetadataDao fileMetadataDao,
            FileDirectionMapper fileDirectionMapper
    ) {
        return new DownloadServiceImpl(fileMetadataDao, fileDirectionMapper);
    }

    @Bean
    TokenGenerator tokenGeneratorImpl(
            @Value("${server.token-key.path}")
            String tokenKeyPath
    ) throws IOException, NoSuchAlgorithmException {
        Path tokenKey = Path.of(AppUtils.getFilePrefixByOs() + tokenKeyPath);
        return new TokenGeneratorImpl(tokenKey);
    }

    @Bean
    ShareFileService shareFileServiceImpl(
            TokenGenerator tokenGenerator,
            FileMetadataDao fileMetadataDao,
            FileMetadataMapper fileMetadataMapper
    ) {
        return new ShareFileServiceImpl(
                tokenGenerator,
                fileMetadataDao,
                fileMetadataMapper
        );
    }
}
