package words.com.fileservicev2.api.controllers;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import words.backend.authmodule.net.models.User;
import words.com.fileservicev2.api.mappers.UploadRespondMapper;
import words.com.fileservicev2.api.responds.UploadRespond;
import words.com.fileservicev2.domain.services.UploadManager;
import words.com.fileservicev2.domain.services.UploadService;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@RestController
@ResponseBody
@RequestMapping("/upload")
public class UploadController {
    private final UploadRespondMapper uploadRespondMapper;
    private final UploadManager uploadManager;
    private final List<UploadService> uploadServices;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UploadRespond uploadFile(
            @AuthenticationPrincipal User user,
            @Parameter(
                    description = "File to upload",
                    required = true,
                    schema = @Schema(type = "string", format = "binary")
            )
            @RequestPart("file") MultipartFile file
    ) throws IOException {
        return uploadRespondMapper.toRespond(uploadManager.upload(file, user));
    }

    @GetMapping("/supported-extensions")
    public List<String> getSupportedExtensions() {
        return uploadServices.stream()
                .map(UploadService::getSupportedExtensions)
                .flatMap(Set::stream)
                .toList();
    }

}
