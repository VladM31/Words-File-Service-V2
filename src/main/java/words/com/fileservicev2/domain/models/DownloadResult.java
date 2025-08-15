package words.com.fileservicev2.domain.models;

public record DownloadResult(
        byte[] content,
        String extension
) {
}
