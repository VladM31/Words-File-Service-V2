package words.com.fileservicev2.domain.services;

import org.springframework.web.multipart.MultipartFile;
import words.backend.authmodule.net.models.User;
import words.com.fileservicev2.domain.models.UploadResult;

import java.io.IOException;

public interface UploadManager {


    UploadResult upload(MultipartFile file, User user) throws IOException;
}
