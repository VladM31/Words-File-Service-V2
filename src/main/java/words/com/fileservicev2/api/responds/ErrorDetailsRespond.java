package words.com.fileservicev2.api.responds;

public record ErrorDetailsRespond(
        String message,
        String exceptionName
) {
}
