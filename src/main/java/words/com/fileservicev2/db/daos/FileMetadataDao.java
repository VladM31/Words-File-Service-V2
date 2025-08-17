package words.com.fileservicev2.db.daos;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import words.com.fileservicev2.db.entities.FileMetadataEntity;
import words.com.fileservicev2.db.searches.FileMetadataSearch;

import java.util.Collection;
import java.util.List;

public interface FileMetadataDao {

    Page<FileMetadataEntity> findBy(FileMetadataSearch search, Pageable pageable);

    List<FileMetadataEntity> findBy(FileMetadataSearch search);

    boolean exists(FileMetadataSearch search);

    void save(FileMetadataEntity fileMetadataEntity);

    void saveAll(Collection<FileMetadataEntity> fileMetadataEntities);

    void update(FileMetadataEntity fileMetadataEntity);

    void incrementCount(String fileMetadataId);
}
