package words.com.fileservicev2.domain.models;

import words.backend.authmodule.net.models.User;
import words.com.fileservicev2.domain.models.enums.Language;

public record AudioGenerationOptions(
        String text,
        Language language,
        User user
) {
}
