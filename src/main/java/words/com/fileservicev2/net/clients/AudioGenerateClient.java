package words.com.fileservicev2.net.clients;

import words.com.fileservicev2.net.requests.TextToAudioRequest;
import words.com.fileservicev2.net.responds.TextToAudioRespond;

public interface AudioGenerateClient {

    TextToAudioRespond create(TextToAudioRequest request);
}
