package words.com.fileservicev2.domain.services.impls;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import words.com.fileservicev2.db.daos.FileMetadataDao;
import words.com.fileservicev2.db.entities.FileMetadataEntity;
import words.com.fileservicev2.db.searches.FileMetadataSearch;
import words.com.fileservicev2.domain.mappers.FileDirectionMapper;
import words.com.fileservicev2.domain.services.FileDeleteService;

import java.nio.file.Files;
import java.time.OffsetDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@RequiredArgsConstructor
class FileDeleteServiceImpl implements FileDeleteService, AutoCloseable {
    private final ExecutorService executorService = Executors.newFixedThreadPool(4);
    private final FileMetadataDao fileMetadataDao;
    private final FileDirectionMapper fileDirectionMapper;

    @Override
    public void execute() {
        var search = FileMetadataSearch.builder()
                .needDeleted(true)
                .hasDeletedAt(false)
                .build();
        var entities = fileMetadataDao.findBy(search);
        if (entities.isEmpty()) {
            return;
        }
        var futures = entities.stream()
                .map(it -> CompletableFuture.runAsync(() -> deleteFile(it), executorService))
                .toList();
        CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();
        fileMetadataDao.updateAll(entities);
        log.info("Deleted {} files", entities.size());
    }

    private void deleteFile(FileMetadataEntity metadataEntity) {
        try {
            var directory = fileDirectionMapper.getDirectoryPath(metadataEntity.getDirectory())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid directory for file: " + metadataEntity.getDirectory()));

            var filePath = directory.resolve(metadataEntity.getFilename());
            Files.deleteIfExists(filePath);
            metadataEntity.setDeletedAt(OffsetDateTime.now());
        } catch (Exception e) {
            log.error("Error during file deletion", e);
        }
    }

    @Override
    public void close() throws Exception {

    }
}
