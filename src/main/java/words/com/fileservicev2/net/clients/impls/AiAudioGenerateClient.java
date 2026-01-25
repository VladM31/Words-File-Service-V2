package words.com.fileservicev2.net.clients.impls;

import lombok.RequiredArgsConstructor;
import org.springframework.web.client.RestTemplate;
import words.com.fileservicev2.domain.models.enums.Language;
import words.com.fileservicev2.net.clients.AudioGenerateClient;
import words.com.fileservicev2.net.requests.TextToAudioRequest;
import words.com.fileservicev2.net.responds.TextToAudioRespond;

import java.util.Map;

@RequiredArgsConstructor
class AiAudioGenerateClient implements AudioGenerateClient {
    private static final Map<Language, String> MODEL_BY_LANGUAGE = Map.of(
            Language.ENGLISH, "tts-1",
            Language.CZECH, "voice-cz-model.onnx",
            Language.POLISH, "pl_PL-gosia-medium.onnx",
            Language.GERMAN, "voice-de-eva_k-x-low",
            Language.FRENCH, "voice-fr-siwis-low",
            Language.UKRAINIAN, "voice-uk-lada-x-low"
    );

    private final RestTemplate restTemplate = new RestTemplate();
    private final String baseUrl;

    @Override
    public TextToAudioRespond create(TextToAudioRequest request) {
        Map<String, String> params = Map.of(
                "model", MODEL_BY_LANGUAGE.getOrDefault(request.language(), "tts-1"),
                "input", request.text()
        );
        var content = restTemplate.postForObject(baseUrl, params, byte[].class);
        return new TextToAudioRespond(content, "wav");
    }
}
