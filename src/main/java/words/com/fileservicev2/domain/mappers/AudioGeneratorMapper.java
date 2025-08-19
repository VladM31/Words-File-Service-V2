package words.com.fileservicev2.domain.mappers;

import org.springframework.stereotype.Component;
import words.com.fileservicev2.domain.models.AudioGenerationOptions;
import words.com.fileservicev2.net.requests.TextToAudioRequest;

@Component
public class AudioGeneratorMapper {

    public TextToAudioRequest toRequest(AudioGenerationOptions options) {
        return new TextToAudioRequest(
                options.text(),
                options.language()
        );

    }
}
