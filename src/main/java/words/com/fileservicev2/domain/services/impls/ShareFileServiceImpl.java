package words.com.fileservicev2.domain.services.impls;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.util.CollectionUtils;
import words.com.fileservicev2.db.daos.FileMetadataDao;
import words.com.fileservicev2.db.entities.FileMetadataEntity;
import words.com.fileservicev2.db.searches.FileMetadataSearch;
import words.com.fileservicev2.domain.mappers.FileMetadataMapper;
import words.com.fileservicev2.domain.models.ImportOptions;
import words.com.fileservicev2.domain.models.ShareOptions;
import words.com.fileservicev2.domain.models.SharedResult;
import words.com.fileservicev2.domain.services.ShareFileService;
import words.com.fileservicev2.domain.services.TokenGenerator;
import words.com.fileservicev2.utils.AppUtils;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
class ShareFileServiceImpl implements ShareFileService {
    private final TokenGenerator tokenGenerator;
    private final FileMetadataDao fileMetadataDao;
    private final FileMetadataMapper fileMetadataMapper;

    @Override
    public SharedResult share(ShareOptions options) {
        if (CollectionUtils.isEmpty(options.filenames())) {
            return new SharedResult(Map.of(), OffsetDateTime.now(AppUtils.APP_ZONE_ID));
        }

        var search = FileMetadataSearch.builder().ownerId(options.user().id())
                .fileNames(options.filenames())
                .build();
        var metadataEntities = fileMetadataDao.findBy(search);

        if (CollectionUtils.isEmpty(metadataEntities)) {
            return new SharedResult(Map.of(), OffsetDateTime.now(AppUtils.APP_ZONE_ID));
        }

        HashMap<String, String> sharedData = new HashMap<>();
        for (var metadata : metadataEntities) {
            String token = tokenGenerator.create(Duration.ofMinutes(options.minutes()), metadata.getFilename());
            sharedData.put(metadata.getFilename(), token);
        }

        return new SharedResult(
                sharedData,
                OffsetDateTime.now(AppUtils.APP_ZONE_ID)
        );
    }

    @Override
    public void importSharedFiles(ImportOptions options) {
        if (CollectionUtils.isEmpty(options.tokens())) {
            return;
        }

        var entities = new ArrayList<FileMetadataEntity>();

        for (var token : options.tokens()) {
            String filename = tokenGenerator.parse(token);
            if (filename == null) {
                continue;
            }

            var search = FileMetadataSearch.builder()
                    .fileName(filename)
                    .build();
            var entityOpt = fileMetadataDao.findBy(search, Pageable.ofSize(1))
                    .stream().findFirst();

            entityOpt.ifPresent(e -> entities.add(fileMetadataMapper.toImportedEntity(e, options.user())));
        }

        fileMetadataDao.saveAll(entities);

    }
}
