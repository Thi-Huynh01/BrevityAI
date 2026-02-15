package com.brevityai.backend.whisper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;

@Service
public class WhisperService {

    private final String apiKey = System.getenv("OPENAI_API_KEY");

    public String transcribeAudio(File audioFile) throws Exception {

        String boundary = "----JavaBoundary" + System.currentTimeMillis();
        String LINE_FEED = "\r\n";

        var byteArrays = new java.util.ArrayList<byte[]>();

        // model part
        String modelPart = "--" + boundary + LINE_FEED +
                "Content-Disposition: form-data; name=\"model\"" + LINE_FEED + LINE_FEED +
                "whisper-1" + LINE_FEED;
        byteArrays.add(modelPart.getBytes());

        // file part
        String filePartHeader = "--" + boundary + LINE_FEED +
                "Content-Disposition: form-data; name=\"file\"; filename=\"" + audioFile.getName() + "\"" + LINE_FEED +
                "Content-Type: " + Files.probeContentType(audioFile.toPath()) + LINE_FEED + LINE_FEED;
        byteArrays.add(filePartHeader.getBytes());
        byteArrays.add(Files.readAllBytes(audioFile.toPath()));
        byteArrays.add(LINE_FEED.getBytes());

        // end boundary
        String endBoundary = "--" + boundary + "--" + LINE_FEED;
        byteArrays.add(endBoundary.getBytes());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openai.com/v1/audio/transcriptions"))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .POST(HttpRequest.BodyPublishers.ofByteArrays(byteArrays))
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(response.body());

        if (node.get("text") == null) {
            throw new RuntimeException("Whisper failed: " + response.body());
        }

        return node.get("text").asText();
    }
}

