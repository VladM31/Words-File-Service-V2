package words.com.fileservicev2.domain.services;

import words.com.fileservicev2.domain.models.DownloadOptions;
import words.com.fileservicev2.domain.models.DownloadResult;

public interface DownloadService {

    DownloadResult downloadFile(DownloadOptions options);
}
