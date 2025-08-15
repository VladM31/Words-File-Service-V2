package words.com.fileservicev2.domain.exceptions;

public class UploadFileException extends RuntimeException {

    public UploadFileException(String message) {
        super(message);
    }

    public UploadFileException(String message, Throwable cause) {
        super(message, cause);
    }
}
