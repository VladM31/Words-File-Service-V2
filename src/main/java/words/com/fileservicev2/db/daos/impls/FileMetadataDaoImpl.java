package words.com.fileservicev2.db.daos.impls;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import words.com.fileservicev2.db.daos.FileMetadataDao;
import words.com.fileservicev2.db.entities.FileMetadataEntity;
import words.com.fileservicev2.db.searches.FileMetadataSearch;
import words.com.fileservicev2.utils.AppUtils;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
class FileMetadataDaoImpl implements FileMetadataDao {
    private final FileMetadataRepository repository;

    @Override
    public Page<FileMetadataEntity> findBy(FileMetadataSearch search, Pageable pageable) {
        return repository.findAll(search, pageable);
    }

    @Override
    public List<FileMetadataEntity> findBy(FileMetadataSearch search) {
        return repository.findAll(search);
    }

    @Override
    public boolean exists(FileMetadataSearch search) {
        return repository.exists(search);
    }

    @Override
    public void save(FileMetadataEntity fileMetadataEntity) {
        repository.save(fileMetadataEntity);
    }

    @Override
    public void saveAll(Collection<FileMetadataEntity> fileMetadataEntities) {
        if (fileMetadataEntities.isEmpty()) {
            return;
        }
        repository.saveAll(fileMetadataEntities);
    }

    @Override
    public void update(FileMetadataEntity fileMetadataEntity) {
        repository.save(fileMetadataEntity);
    }

    @Override
    public void updateAll(Collection<FileMetadataEntity> fileMetadataEntities) {
        if (fileMetadataEntities.isEmpty()) {
            return;
        }
        repository.saveAll(fileMetadataEntities);
    }

    @Override
    public void incrementCount(String fileMetadataId) {
        repository.incrementCount(fileMetadataId, OffsetDateTime.now(AppUtils.APP_ZONE_ID));
    }
}
