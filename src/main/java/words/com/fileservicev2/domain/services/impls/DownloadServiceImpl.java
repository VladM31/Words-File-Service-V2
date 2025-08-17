package words.com.fileservicev2.domain.services.impls;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import words.backend.authmodule.net.models.Role;
import words.com.fileservicev2.db.daos.FileMetadataDao;
import words.com.fileservicev2.db.enums.LogicalOperator;
import words.com.fileservicev2.db.searches.FileMetadataSearch;
import words.com.fileservicev2.domain.mappers.FileDirectionMapper;
import words.com.fileservicev2.domain.models.DownloadOptions;
import words.com.fileservicev2.domain.models.DownloadResult;
import words.com.fileservicev2.domain.services.DownloadService;

import java.io.IOException;
import java.nio.file.Files;

@RequiredArgsConstructor
class DownloadServiceImpl implements DownloadService {
    private final FileMetadataDao fileMetadataDao;
    private final FileDirectionMapper fileDirectionMapper;

    @Override
    public DownloadResult downloadFile(DownloadOptions options) {
        var searchBuilder = FileMetadataSearch.builder().fileName(options.fileName());

        if (options.user().role() != Role.ADMINISTRATION) {
            searchBuilder.subSearch(FileMetadataSearch.builder()
                    .ownerId(options.user().id())
                    .hasOwnerId(false)
                    .logicalOperator(LogicalOperator.OR)
                    .build());
        }
        var metadataEntity = fileMetadataDao.findBy(searchBuilder.build(), Pageable.ofSize(1))
                .stream().findFirst().orElseThrow(() -> new IllegalArgumentException("File not found: " + options.fileName()));
        var directory = fileDirectionMapper.getDirectoryPath(metadataEntity.getDirectory())
                .orElseThrow(() -> new IllegalArgumentException("Invalid directory for file: " + metadataEntity.getDirectory()));

        var filePath = directory.resolve(metadataEntity.getFilename());
        if (!filePath.toFile().exists()) {
            throw new IllegalArgumentException("File does not exist: " + filePath);
        }

        try {
            var content = Files.readAllBytes(filePath);
            return new DownloadResult(content, metadataEntity.getExtension());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}
