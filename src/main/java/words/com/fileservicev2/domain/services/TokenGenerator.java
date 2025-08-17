package words.com.fileservicev2.domain.services;

import java.time.Duration;

public interface TokenGenerator {

    String create(Duration ttl, String plaintext);

    String parse(String token);
}
