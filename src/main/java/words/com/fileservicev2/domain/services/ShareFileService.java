package words.com.fileservicev2.domain.services;

import words.com.fileservicev2.domain.models.ImportOptions;
import words.com.fileservicev2.domain.models.ShareOptions;
import words.com.fileservicev2.domain.models.SharedResult;

public interface ShareFileService {

    SharedResult share(ShareOptions options);

    void importSharedFiles(ImportOptions options);
}
