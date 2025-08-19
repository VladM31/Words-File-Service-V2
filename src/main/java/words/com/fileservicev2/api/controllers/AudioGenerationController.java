package words.com.fileservicev2.api.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import words.backend.authmodule.net.models.User;
import words.com.fileservicev2.api.mappers.AudioGenerationApiMapper;
import words.com.fileservicev2.api.mappers.UploadRespondMapper;
import words.com.fileservicev2.api.request.AudioGenerationRequest;
import words.com.fileservicev2.api.responds.UploadRespond;
import words.com.fileservicev2.domain.services.AudioGenerator;

@Slf4j
@RequiredArgsConstructor
@RestController
@ResponseBody
@RequestMapping("/text-to-audio")
public class AudioGenerationController {
    private final AudioGenerationApiMapper audioGenerationApiMapper;
    private final UploadRespondMapper uploadRespondMapper;
    private final AudioGenerator audioGenerator;


    @PostMapping
    public UploadRespond generateAudio(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody AudioGenerationRequest request
    ) {
        var options = audioGenerationApiMapper.toOptions(user, request);
        var result = audioGenerator.generate(options);
        return uploadRespondMapper.toRespond(result);
    }
}
