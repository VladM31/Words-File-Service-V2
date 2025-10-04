package words.com.fileservicev2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication(exclude = {UserDetailsServiceAutoConfiguration.class})
public class FileServiceV2Application {

    public static void main(String[] args) {
        SpringApplication.run(FileServiceV2Application.class, args);
    }

}
