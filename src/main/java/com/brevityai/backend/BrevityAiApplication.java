package com.brevityai.backend;

import ch.qos.logback.core.net.SyslogOutputStream;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@SpringBootApplication
public class BrevityAiApplication {

    public static void main(String[] args) throws IOException, InterruptedException {

        SpringApplication.run(BrevityAiApplication.class, args);

        // Testing OpenAI key
//
//        String apiKey = System.getenv("OPENAI_API_KEY");
//        HttpRequest request = HttpRequest.newBuilder()
//                .uri(URI.create("https://api.openai.com/v1/models"))
//                .header("Authorization", "Bearer " + apiKey)
//                .GET()
//                .build();
//
//        HttpClient client = HttpClient.newHttpClient();
//        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//        System.out.println("OpenAI Response: " + response.body());
    }

}
