package words.com.fileservicev2.domain.exceptions;

public class GifUploadException extends UploadFileException {
    public GifUploadException(String message) {
        super(message);
    }

    public GifUploadException(String message, Throwable cause) {
        super(message, cause);
    }
}
