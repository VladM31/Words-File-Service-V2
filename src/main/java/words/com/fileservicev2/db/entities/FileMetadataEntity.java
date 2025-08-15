package words.com.fileservicev2.db.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import words.com.fileservicev2.domain.models.enums.CreationType;
import words.com.fileservicev2.domain.models.enums.FileDirectory;

import java.time.OffsetDateTime;

@Data
@Entity
@Table
@NoArgsConstructor
@AllArgsConstructor
public class FileMetadataEntity {
    @Id
    @Column(updatable = false)
    private String id;
    @Column(updatable = false)
    private String filename;
    @Column(updatable = false)
    private String ownerId;
    @Column(updatable = false)
    private String extension;

    @Enumerated(EnumType.STRING)
    private FileDirectory directory;
    @Enumerated(EnumType.STRING)
    private CreationType creationType;

    private long usageCount;
    @CreatedDate
    @Column(updatable = false)
    private OffsetDateTime createdAt;
    @LastModifiedBy
    private OffsetDateTime updatedAt;
}
