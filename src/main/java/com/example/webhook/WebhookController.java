package com.example.webhook;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.Date;

@RestController
public class WebhookController {
    private static final String IMAGE = "phanthanhsum/airlab:latest";
    private static final String CONTAINER = "airlab";

    @PostMapping("/dockerhub-webhook")
    public String deploy() throws IOException, InterruptedException {

        System.out.println("Webhook received from DockerHub");
        System.out.println(LocalDate.now());
        run("docker pull " + IMAGE);
        run("docker stop " + CONTAINER + " || true");
        run("docker rm " + CONTAINER + " || true");

        run("""
            docker run -d -p 80:8080 \
            --env-file /opt/airlab/.env \
            --restart unless-stopped \
            --name airlab \
            """ + IMAGE
        );

        return "OK";
    }

    private void run(String cmd) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder("bash", "-c", cmd);
        pb.inheritIO();
        System.out.println(pb.command().toString());
        Process p = pb.start();
        p.waitFor();
    }
}
