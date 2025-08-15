package words.com.fileservicev2.domain.services;

import java.io.IOException;

public interface ImageContentAnalyzer {

    boolean isUnValid(byte[] imageBytes, String format) throws IOException;

    String getName();
}
