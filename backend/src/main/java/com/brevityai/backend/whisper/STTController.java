package com.brevityai.backend.whisper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;

//TEST CLASS - testing the whisper connection and transcribing.

@RestController
@RequestMapping("/api/stt")
public class STTController {

    private final String OPENAI_API_KEY = System.getenv("OPENAI_API_KEY");

    @PostMapping
    public ResponseEntity<?> transcribeAudio(@RequestParam("file") MultipartFile file) {
        try {
            File tempFile = File.createTempFile("user_audio",".wav");
            file.transferTo(tempFile);

            String boundary = "----JavaBoundary" + System.currentTimeMillis();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.openai.com/v1/audio/transcriptions"))
                    .header("Authorization", "Bearer " + OPENAI_API_KEY)
                    .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                    .POST(ofMimeMultipart(tempFile, boundary))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(response.body());
            String transcript = node.get("text").asText();

            return ResponseEntity.ok(Map.of("transcript", transcript));
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error transcribing Audio: " + e.getMessage());
        }
    }

    // Helper function for HttpRequest builder
    public static HttpRequest.BodyPublisher ofMimeMultipart(File file, String boundary) throws IOException {
        var byteArrays = new java.util.ArrayList<byte[]>();

        String LINE_FEED = "\r\n";

        String modelPart = "--" + boundary + LINE_FEED +
                "Content-Disposition: form-data; name=\"model\"" + LINE_FEED + LINE_FEED +
                "whisper-1" + LINE_FEED;
        byteArrays.add(modelPart.getBytes(StandardCharsets.UTF_8));

        String filePartHeader = "--" + boundary + LINE_FEED +
                "Content-Disposition: form-data; name=\"file\"; filename=\"" + file.getName() + "\"" + LINE_FEED +
                "Content-Type: " + Files.probeContentType(file.toPath()) + LINE_FEED + LINE_FEED;
        byteArrays.add(filePartHeader.getBytes(StandardCharsets.UTF_8));
        byteArrays.add(Files.readAllBytes(file.toPath()));
        byteArrays.add(LINE_FEED.getBytes(StandardCharsets.UTF_8));

        String endBoundary = "--" + boundary + "--" + LINE_FEED;
        byteArrays.add(endBoundary.getBytes(StandardCharsets.UTF_8));

        return HttpRequest.BodyPublishers.ofByteArrays(byteArrays);
    }
}
