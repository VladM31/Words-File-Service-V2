package words.com.fileservicev2.domain.services.impls;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import words.backend.authmodule.net.models.User;
import words.com.fileservicev2.db.daos.FileMetadataDao;
import words.com.fileservicev2.domain.exceptions.UploadFileException;
import words.com.fileservicev2.domain.mappers.FileMetadataMapper;
import words.com.fileservicev2.domain.models.UploadResult;
import words.com.fileservicev2.domain.services.UploadManager;
import words.com.fileservicev2.domain.services.UploadService;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
class UploadManagerImpl implements UploadManager {
    private final List<UploadService> uploadServices;
    private final FileMetadataDao fileMetadataDao;
    private final FileMetadataMapper fileMetadataMapper;

    @Override
    public UploadResult upload(MultipartFile file, User user) throws IOException {
        log.info("User {} is uploading file: {}", user.phoneNumber(), file.getOriginalFilename());
        var fileName = file.getOriginalFilename();
        if (!StringUtils.hasText(fileName)) {
            throw new UploadFileException("File name is empty.");
        }

        UploadService uploadService = uploadServices.stream()
                .filter(service -> service.canUpload(fileName))
                .findFirst()
                .orElseThrow(() -> new UploadFileException("Unsupported file type."));

        var result = uploadService.upload(file, user);
        var entity = fileMetadataMapper.toEntity(result, user);
        fileMetadataDao.save(entity);
        log.info("File uploaded: {}", result.fileName());

        return result;
    }
}
