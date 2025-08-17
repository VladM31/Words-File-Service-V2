package words.com.fileservicev2.domain.models;

import lombok.Builder;
import words.backend.authmodule.net.models.User;

@Builder
public record DownloadOptions(
        User user,
        String fileName
) {
}
