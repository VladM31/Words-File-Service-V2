package words.com.fileservicev2.domain.models;

import java.time.OffsetDateTime;
import java.util.Map;

public record SharedResult(
        Map<String, String> data,
        OffsetDateTime createdAt
) {
}
