package words.com.fileservicev2.domain.models;

import words.backend.authmodule.net.models.User;

import java.util.Collection;

public record ImportOptions(
        Collection<String> tokens,
        User user
) {
}
