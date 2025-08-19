package words.com.fileservicev2.domain.services.impls;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import words.com.fileservicev2.db.daos.FileMetadataDao;
import words.com.fileservicev2.domain.mappers.AudioGeneratorMapper;
import words.com.fileservicev2.domain.mappers.FileMetadataMapper;
import words.com.fileservicev2.domain.models.AudioGenerationOptions;
import words.com.fileservicev2.domain.models.UploadResult;
import words.com.fileservicev2.domain.services.AudioGenerator;
import words.com.fileservicev2.domain.services.FileNameGenerator;
import words.com.fileservicev2.net.clients.AudioGenerateClient;
import words.com.fileservicev2.utils.FileDeleteCloseable;
import ws.schild.jave.Encoder;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.encode.AudioAttributes;
import ws.schild.jave.encode.EncodingAttributes;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

@RequiredArgsConstructor
class AudioGeneratorImpl implements AudioGenerator {
    private final AudioGeneratorMapper audioGeneratorMapper;
    private final AudioGenerateClient audioGenerateClient;
    private final FileNameGenerator fileNameGenerator;
    private final Path directory;
    private final Path tempDirectory;
    private final FileMetadataDao fileMetadataDao;
    private final FileMetadataMapper fileMetadataMapper;

    @PostConstruct
    void init() throws IOException {
        if (Files.notExists(directory)) {
            Files.createDirectories(directory);
        }
        if (Files.notExists(tempDirectory)) {
            Files.createDirectories(tempDirectory);
        }
    }


    @Override
    public UploadResult generate(AudioGenerationOptions options) {
        var respond = audioGenerateClient.create(
                audioGeneratorMapper.toRequest(options)
        );
        var content = respond.content();

        if ("wav".equals(respond.extension())) {
            content = mapWavToMp3(content);
        }

        var newFileName = fileNameGenerator.generate(options.user().id()) + ".mp3";
        Path outputPath = directory.resolve(newFileName);

        try {
            Files.write(outputPath, content, StandardOpenOption.CREATE_NEW);
            var result = new UploadResult(newFileName);
            var entity = fileMetadataMapper.toEntity(result, options.user());
            fileMetadataDao.save(entity);

            return result;
        } catch (IOException e) {
            throw new UncheckedIOException("Error creating output file", e);
        }
    }

    private byte[] mapWavToMp3(byte[] content) {
        try {
            if (content == null || content.length == 0) {
                throw new IOException("Failed to generate audio content");
            }

            var sourcePath = Files.createTempFile(tempDirectory, "source_", ".wav");
            var source = sourcePath.toFile();
            var target = Files.createTempFile(tempDirectory, "audio_", ".mp3");
            try (FileDeleteCloseable sourceCloseable = new FileDeleteCloseable(sourcePath);
                 FileDeleteCloseable targetCloseable = new FileDeleteCloseable(target)) {
                Files.write(source.toPath(), content, StandardOpenOption.TRUNCATE_EXISTING);
                convertWavToMp3(source, target.toFile());
                return Files.readAllBytes(target);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Error during audio conversion", e);
        }
    }

    public static void convertWavToMp3(File source, File target) throws IOException {
        AudioAttributes audio = new AudioAttributes();
        audio.setCodec("libmp3lame");
        audio.setBitRate(128000);
        audio.setChannels(2);
        audio.setSamplingRate(44100);

        EncodingAttributes attrs = new EncodingAttributes();
        attrs.setOutputFormat("mp3");
        attrs.setAudioAttributes(audio);

        Encoder encoder = new Encoder();
        try {
            encoder.encode(new MultimediaObject(source), target, attrs);
            System.out.println("Конвертация завершена успешно.");
        } catch (Exception e) {
            throw new IOException("Ошибка конвертации: " + e.getMessage(), e);
        }
    }
}
