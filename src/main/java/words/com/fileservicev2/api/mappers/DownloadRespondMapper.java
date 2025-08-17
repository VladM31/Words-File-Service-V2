package words.com.fileservicev2.api.mappers;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import words.com.fileservicev2.domain.models.DownloadResult;

@Component
public class DownloadRespondMapper {


    public ResponseEntity<Resource> toResponseEntity(DownloadResult result) {
        ByteArrayResource resource = new ByteArrayResource(result.content());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"file." + result.extension() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}
