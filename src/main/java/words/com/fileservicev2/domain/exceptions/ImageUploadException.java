package words.com.fileservicev2.domain.exceptions;

public class ImageUploadException extends UploadFileException {

    public ImageUploadException(String message) {
        super(message);
    }

    public ImageUploadException(String message, Throwable cause) {
        super(message, cause);
    }
}
