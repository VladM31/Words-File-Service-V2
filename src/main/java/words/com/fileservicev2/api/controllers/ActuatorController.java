package words.com.fileservicev2.api.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
public class ActuatorController {
    @Value("${server.servlet.context-path}")
    private String contextPath;

    @GetMapping("/actuator/info")
    public ResponseEntity<Void> redirectToExternal() {
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(contextPath + "/swagger-ui.html"));
        return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
    }
}
