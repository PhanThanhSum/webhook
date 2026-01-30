package com.example.webhook;

public class DockerHubWebhook {
    public PushData push_data;

    static class PushData {
        public String tag;
    }
}
