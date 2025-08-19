package words.com.fileservicev2.domain.services;

import words.com.fileservicev2.domain.models.AudioGenerationOptions;
import words.com.fileservicev2.domain.models.UploadResult;

public interface AudioGenerator {

    UploadResult generate(AudioGenerationOptions options);
}
