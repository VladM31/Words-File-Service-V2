package words.com.fileservicev2.net.requests;

import words.com.fileservicev2.domain.models.enums.Language;

public record TextToAudioRequest(
        String text,
        Language language
) {
}
