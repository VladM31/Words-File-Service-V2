package words.com.fileservicev2.api.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import words.backend.authmodule.net.models.User;
import words.com.fileservicev2.api.mappers.DownloadRespondMapper;
import words.com.fileservicev2.domain.models.DownloadOptions;
import words.com.fileservicev2.domain.services.DownloadService;

@Slf4j
@RequiredArgsConstructor
@RestController
@ResponseBody
@RequestMapping("/download")
public class DownloadController {
    private final DownloadRespondMapper downloadRespondMapper;
    private final DownloadService downloadService;

    @GetMapping("/{fileName}")
    ResponseEntity<Resource> downloadFile(
            @AuthenticationPrincipal User user,
            @PathVariable String fileName
    ) {
        var result = downloadService.downloadFile(
                DownloadOptions.builder()
                        .user(user)
                        .fileName(fileName)
                        .build()
        );
        return downloadRespondMapper.toResponseEntity(result);
    }
}
