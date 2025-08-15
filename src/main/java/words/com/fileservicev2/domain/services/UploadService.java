package words.com.fileservicev2.domain.services;

import org.springframework.web.multipart.MultipartFile;
import words.backend.authmodule.net.models.User;
import words.com.fileservicev2.domain.models.UploadResult;
import words.com.fileservicev2.domain.models.enums.FileDirectory;

import java.io.IOException;
import java.util.Set;

public interface UploadService {

    boolean canUpload(String fileName);

    UploadResult upload(MultipartFile file, User user) throws IOException;

    Set<String> getSupportedExtensions();

    FileDirectory getFileDirectory();
}
