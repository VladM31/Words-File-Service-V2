package words.com.fileservicev2.api.mappers;

import org.springframework.stereotype.Component;
import words.com.fileservicev2.api.responds.UploadRespond;
import words.com.fileservicev2.domain.models.UploadResult;


@Component
public class UploadRespondMapper {

    public UploadRespond toRespond(UploadResult result) {
        return new UploadRespond(result.fileName());
    }
}
