package words.com.fileservicev2.db.daos.impls;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import words.com.fileservicev2.db.entities.FileMetadataEntity;

import java.time.OffsetDateTime;

interface FileMetadataRepository extends ListCrudRepository<FileMetadataEntity,String>,
        JpaSpecificationExecutor<FileMetadataEntity> {


    @Modifying
    @Query("UPDATE FileMetadataEntity f SET f.usageCount = f.usageCount + 1, f.updatedAt = :updatedAt WHERE f.id = :fileMetadataId")
    void incrementCount(String fileMetadataId, OffsetDateTime updatedAt);
}
