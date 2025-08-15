package words.com.fileservicev2.domain.mappers;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import words.backend.authmodule.net.models.Role;
import words.backend.authmodule.net.models.User;
import words.com.fileservicev2.db.entities.FileMetadataEntity;
import words.com.fileservicev2.domain.models.UploadResult;
import words.com.fileservicev2.domain.models.enums.CreationType;
import words.com.fileservicev2.domain.models.enums.FileDirectory;
import words.com.fileservicev2.domain.services.UploadService;
import words.com.fileservicev2.utils.AppUtils;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class FileMetadataMapper {
    private final Map<String, FileDirectory> fileDirectoryMap = new HashMap<>();
    private final List<UploadService> uploadServices;

    @PostConstruct
    private void init() {
        for (UploadService uploadService : uploadServices) {
            for (String supportedExtension : uploadService.getSupportedExtensions()) {
                fileDirectoryMap.put(supportedExtension, uploadService.getFileDirectory());
            }
        }
    }

    public FileMetadataEntity toEntity(UploadResult uploadResult, User user) {
        FileMetadataEntity fileMetadataEntity = new FileMetadataEntity();
        fileMetadataEntity.setId(UUID.randomUUID().toString());

        if (user.role() != Role.ADMINISTRATION) {
            fileMetadataEntity.setOwnerId(user.id());
        }

        fileMetadataEntity.setFilename(uploadResult.fileName());
        fileMetadataEntity.setExtension(getExtension(uploadResult.fileName()));
        fileMetadataEntity.setDirectory(fileDirectoryMap.get(fileMetadataEntity.getExtension()));
        fileMetadataEntity.setCreationType(CreationType.UPLOADED);
        fileMetadataEntity.setUsageCount(0);
        fileMetadataEntity.setCreatedAt(OffsetDateTime.now(AppUtils.APP_ZONE_ID));
        fileMetadataEntity.setUpdatedAt(fileMetadataEntity.getCreatedAt());

        return fileMetadataEntity;
    }

    private String getExtension(String fileName) {
        int lastIndexOfDot = fileName.lastIndexOf('.');
        if (lastIndexOfDot == -1 || lastIndexOfDot == fileName.length() - 1) {
            return null; // No extension found
        }
        return fileName.substring(lastIndexOfDot + 1).toLowerCase();
    }
}
