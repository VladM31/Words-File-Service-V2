package words.com.fileservicev2.db.daos.impls;

import org.springframework.context.annotation.Bean;
import words.com.fileservicev2.db.daos.FileMetadataDao;

public class DaoConfig {

    @Bean
    FileMetadataDao fileMetadataDaoImpl(FileMetadataRepository repository){
        return new FileMetadataDaoImpl(repository);
    }
}
