package words.com.fileservicev2.domain.schedulers;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import words.com.fileservicev2.domain.services.FileDeleteService;

@Component
@RequiredArgsConstructor
public class FileDeleteScheduler {
    private final FileDeleteService fileDeleteService;

    @Scheduled(cron = "0 */5 * * * *") // Every 5 minutes
    public void deleteFiles() {
        fileDeleteService.execute();
    }
}
