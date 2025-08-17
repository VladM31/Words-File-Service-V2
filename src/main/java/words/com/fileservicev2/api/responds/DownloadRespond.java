package words.com.fileservicev2.api.responds;

public record DownloadRespond(
        byte[] content,
        String extension
) {
}
