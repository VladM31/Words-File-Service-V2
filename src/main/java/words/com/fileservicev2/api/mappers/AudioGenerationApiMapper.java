package words.com.fileservicev2.api.mappers;

import org.springframework.stereotype.Component;
import words.backend.authmodule.net.models.User;
import words.com.fileservicev2.api.request.AudioGenerationRequest;
import words.com.fileservicev2.domain.models.AudioGenerationOptions;

@Component
public class AudioGenerationApiMapper {

    public AudioGenerationOptions toOptions(User user, AudioGenerationRequest request) {
        return new AudioGenerationOptions(
                request.text(),
                request.language(),
                user
        );
    }
}
