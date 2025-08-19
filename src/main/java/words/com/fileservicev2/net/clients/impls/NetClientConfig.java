package words.com.fileservicev2.net.clients.impls;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import words.com.fileservicev2.net.clients.AudioGenerateClient;

public class NetClientConfig {

    @Bean
    AudioGenerateClient AiAudioGenerateClient(
            @Value("${ai.generate.audio.url}")
            String aiGenerateAudioUrl
    ) {
        return new AiAudioGenerateClient(aiGenerateAudioUrl);
    }
}
