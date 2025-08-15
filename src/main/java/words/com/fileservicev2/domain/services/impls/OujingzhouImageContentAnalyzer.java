package words.com.fileservicev2.domain.services.impls;

import com.oujingzhou.censor.ImageCensor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import words.com.fileservicev2.domain.services.ImageContentAnalyzer;
import words.com.fileservicev2.domain.utils.AppUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
class OujingzhouImageContentAnalyzer implements ImageContentAnalyzer {

    private final Path directory;
    private final float minPornProbability;

    @Override
    public boolean isUnValid(byte[] imageBytes, String format) throws IOException {
        if (!Files.exists(directory)) {
            Files.createDirectories(directory);
        }
        File tempFile = File.createTempFile(AppUtils.getFilePrefixByOs() + "temp_image_", "." + format.toLowerCase(),directory.toFile());
        // Инициализация (модель загружается автоматически)
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(imageBytes);
        }

        try (ImageCensor censor = new ImageCensor();
             AutoCloseable fileDelete = tempFile::delete) {

            // Предполагаем, что predict возвращает Map с вероятностями классов
            Map<String, Float> predictions = censor.predict(tempFile.getAbsolutePath());

            // Логика: если вероятность NSFW-классов > 0.5, то false (запрещённый)
            float pornProb = predictions.getOrDefault("Porn", 0f);
            if (pornProb > minPornProbability) {
                log.warn("Detected Porn");
                return true; // Запрещённый контент
            }

            float sexyProb = predictions.getOrDefault("Sexy", 0f);
            if (sexyProb > minPornProbability) {
                log.warn("Detected Sexy");
                return true; // Запрещённый контент
            }

            float hentaiProb = predictions.getOrDefault("Hentai", 0f);
            if (hentaiProb > minPornProbability) {
                log.warn("Detected Hentai");
                return true; // Запрещённый контент
            }

            return false; // Запрещённый контент
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getName() {
        return "Oujingzhou";
    }
}
