package words.com.fileservicev2.domain.mappers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import words.com.fileservicev2.domain.models.enums.FileDirectory;
import words.com.fileservicev2.utils.AppUtils;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class FileDirectionMapper {
    private final Map<FileDirectory, Path> fileDirectoryMap = new HashMap<>();

    public FileDirectionMapper(
            @Value("${server.image.upload.directory}")
            String imageUploadDirectoryPath,
            @Value("${server.audio.upload.directory}")
            String audioUploadDirectoryPath
    ) {
        fileDirectoryMap.put(FileDirectory.IMAGE, Path.of(AppUtils.getFilePrefixByOs() + imageUploadDirectoryPath));
        fileDirectoryMap.put(FileDirectory.AUDIO, Path.of(AppUtils.getFilePrefixByOs() + audioUploadDirectoryPath));

    }

    public Optional<Path> getDirectoryPath(FileDirectory direction) {
        return Optional.ofNullable(fileDirectoryMap.get(direction));
    }
}
