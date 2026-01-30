package com.example.webhook;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDateTime;

@RestController
public class WebhookController {

    private static final String IMAGE = "phanthanhsum/airlab";
    private static final String CONTAINER = "airlab";

    @PostMapping("/dockerhub-webhook")
    public String deploy(@RequestBody DockerHubWebhook body)
            throws IOException, InterruptedException {

        String tag = body.push_data.tag;
        String fullImage = IMAGE + ":" + tag;

        System.out.println("Webhook received");
        System.out.println("Deploying: " + fullImage);
        System.out.println(LocalDateTime.now());

        run("docker pull " + fullImage);
        run("docker stop " + CONTAINER + " || true");
        run("docker rm " + CONTAINER + " || true");

        run("""
            docker run -d -p 80:8080 \
            --env-file /opt/airlab/.env \
            --restart unless-stopped \
            --name airlab \
            """ + fullImage
        );

        // dọn image cũ
        run("docker image prune -af");

        return "DEPLOYED " + fullImage;
    }

    private void run(String cmd) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder("bash", "-c", cmd);
        pb.inheritIO();
        Process p = pb.start();
        p.waitFor();
    }
}

