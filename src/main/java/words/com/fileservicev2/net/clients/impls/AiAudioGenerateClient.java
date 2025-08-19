package words.com.fileservicev2.net.clients.impls;

import lombok.RequiredArgsConstructor;
import org.springframework.web.client.RestTemplate;
import words.com.fileservicev2.net.clients.AudioGenerateClient;
import words.com.fileservicev2.net.requests.TextToAudioRequest;
import words.com.fileservicev2.net.responds.TextToAudioRespond;

import java.util.Map;

@RequiredArgsConstructor
class AiAudioGenerateClient implements AudioGenerateClient {
    private final RestTemplate restTemplate = new RestTemplate();
    private final String baseUrl;

    @Override
    public TextToAudioRespond create(TextToAudioRequest request) {
        Map<String, String> params = Map.of(
                "model", "tts-1",
                "input", request.text()
        );
        var content = restTemplate.postForObject(baseUrl, params, byte[].class);
        return new TextToAudioRespond(content, "wav");
    }
}
