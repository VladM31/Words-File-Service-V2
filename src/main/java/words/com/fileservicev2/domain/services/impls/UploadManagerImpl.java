package words.com.fileservicev2.domain.services.impls;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import words.backend.authmodule.net.models.User;
import words.com.fileservicev2.domain.models.UploadResult;
import words.com.fileservicev2.domain.services.UploadManager;
import words.com.fileservicev2.domain.services.UploadService;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
class UploadManagerImpl implements UploadManager {
    private final List<UploadService> uploadServices;
    @Override
    public UploadResult upload(MultipartFile file, User user) throws IOException {
        return null;
    }
}
