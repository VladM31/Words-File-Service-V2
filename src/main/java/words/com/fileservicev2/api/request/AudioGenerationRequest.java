package words.com.fileservicev2.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import words.com.fileservicev2.domain.models.enums.Language;

public record AudioGenerationRequest(
        @Size(min = 1, max = 255, message = "Text must be between 1 and 255 characters")
        @NotBlank(message = "Text cannot be blank")
        String text,
        @NotNull(message = "Language cannot be null")
        Language language
) {
}
