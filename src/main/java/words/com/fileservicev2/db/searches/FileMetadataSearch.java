package words.com.fileservicev2.db.searches;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import org.springframework.util.CollectionUtils;
import words.com.fileservicev2.db.entities.FileMetadataEntity;
import words.com.fileservicev2.db.enums.LogicalOperator;
import words.com.fileservicev2.domain.models.enums.CreationType;
import words.com.fileservicev2.domain.models.enums.FileDirectory;
import words.com.fileservicev2.utils.Range;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collection;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileMetadataSearch implements Specification<FileMetadataEntity> {
    @Singular(ignoreNullCollections = true)
    private Collection<String> fileMetadataIds;
    @Singular(ignoreNullCollections = true)
    private Collection<String> ownerIds;
    @Singular(ignoreNullCollections = true)
    private Collection<String> fileNames;
    @Singular(ignoreNullCollections = true)
    private Collection<String> extensions;
    private Collection<FileDirectory> directories;
    private Collection<CreationType> creationTypes;

    private Range<Long> usageCount;
    private Range<OffsetDateTime> createdAt;
    private Range<OffsetDateTime> updatedAt;

    private Boolean hasOwnerId;
    private Boolean hasDeletedAt;
    private Boolean needDeleted;


    @Singular("subSearch")
    private Collection<FileMetadataSearch> subSearches;
    private LogicalOperator logicalOperator;

    @Override
    public Predicate toPredicate(@NonNull Root<FileMetadataEntity> root, CriteriaQuery<?> query,@NonNull  CriteriaBuilder cb) {
        ArrayList<Predicate> predicates = new ArrayList<>();

        if (!CollectionUtils.isEmpty(fileMetadataIds)) {
            predicates.add(root.get("id").in(fileMetadataIds));
        }

        if (!CollectionUtils.isEmpty(ownerIds)) {
            predicates.add(root.get("ownerId").in(ownerIds));
        }

        if (!CollectionUtils.isEmpty(fileNames)) {
            predicates.add(root.get("filename").in(fileNames));
        }

        if (!CollectionUtils.isEmpty(extensions)) {
            predicates.add(root.get("extension").in(extensions));
        }

        if (!CollectionUtils.isEmpty(directories)) {
            predicates.add(root.get("directory").in(directories));
        }

        if (!CollectionUtils.isEmpty(creationTypes)) {
            predicates.add(root.get("creationType").in(creationTypes));
        }

        if (Range.hasFrom(usageCount)) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("usageCount"), Range.from(usageCount)));
        }
        if (Range.hasTo(usageCount)) {
            predicates.add(cb.lessThanOrEqualTo(root.get("usageCount"), Range.to(usageCount)));
        }

        if (Range.hasFrom(createdAt)) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), Range.from(createdAt)));
        }
        if (Range.hasTo(createdAt)) {
            predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), Range.to(createdAt)));
        }

        if (Range.hasFrom(updatedAt)) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("updatedAt"), Range.from(updatedAt)));
        }
        if (Range.hasTo(updatedAt)) {
            predicates.add(cb.lessThanOrEqualTo(root.get("updatedAt"), Range.to(updatedAt)));
        }

        if (hasOwnerId == Boolean.FALSE){
            predicates.add(cb.isNull(root.get("ownerId")));
        }
        if (hasOwnerId == Boolean.TRUE) {
            predicates.add(cb.isNotNull(root.get("ownerId")));
        }

        if (hasDeletedAt == Boolean.FALSE) {
            predicates.add(cb.isNull(root.get("deletedAt")));
        }
        if (hasDeletedAt == Boolean.TRUE) {
            predicates.add(cb.isNotNull(root.get("deletedAt")));
        }

        if (needDeleted != null) {
            predicates.add(cb.equal(root.get("needDelete"), needDeleted));
        }

        if (!CollectionUtils.isEmpty(subSearches)) {
            for (FileMetadataSearch subSearch : subSearches) {
                Predicate subPredicate = subSearch.toPredicate(root, query, cb);
                if (subPredicate != null) {
                    predicates.add(subPredicate);
                }
            }
        }

        if (predicates.isEmpty()){
            return null;
        }
        if (logicalOperator == LogicalOperator.OR) {
            return cb.or(predicates.toArray(new Predicate[0]));
        }

        return cb.and(predicates.toArray(new Predicate[0]));
    }
}
