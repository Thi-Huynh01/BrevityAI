package com.brevityai.backend.llm;

import com.brevityai.backend.practice.dto.EvaluationResult;
import com.brevityai.backend.practice.dto.GenerationResult;
import com.brevityai.backend.redis.RedisService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;

@Service
public class LLMService {

    private final String apiKey = System.getenv("OPENAI_API_KEY");

    private final RedisService redisService;

    public LLMService(RedisService redisService) {
        this.redisService = redisService;
    }

    public EvaluationResult evaluate(String transcript, String expected) throws Exception {
        String requestBody = """
        {
          "model": "gpt-4o-mini",
          "messages": [
            {
              "role": "system",
              "content": "You are a Vietnamese pronunciation tutor. You MUST respond ONLY with valid JSON. No extra text."
            },
            {
              "role": "user",
              "content": "User said: %s. Expected: %s. Return ONLY valid JSON in this format: { \\"score\\": number (0-100), \\"accuracy\\": \\"short description\\", \\"mistakes\\": \\"what was wrong\\", \\"suggestion\\": \\"how to improve\\" }"
            }
          ]
        }
        """.formatted(transcript, expected);

        // Build HTTP request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(response.body());

        // Check for error
        if (node.has("error")) {
            throw new RuntimeException("OpenAI Error: " + node.get("error").get("message").asText());
        }

        JsonNode choices = node.get("choices");
        if (choices == null || !choices.isArray() || choices.isEmpty()) {
            throw new RuntimeException("Invalid OpenAI response: " + response.body());
        }

        String content =  choices.get(0).get("message").get("content").asText();

        // Sometimes OpenAI will have extra characters in its response, handle accordingly
        content = content.replace("```json", "")
                .replace("```", "")
                .trim();

        JsonNode feedback = mapper.readTree(content);
        EvaluationResult result = new EvaluationResult();

        // Set DTO so that it can be parsed as JSON
        result.setScore(feedback.get("score").asInt());
        result.setMistakes(feedback.get("mistakes").asText());
        result.setAccuracy(feedback.get("accuracy").asText());
        result.setSuggestion(feedback.get("suggestion").asText());

        return result;
    }

    public GenerationResult generateSentence(String difficulty) throws IOException, InterruptedException {
        String randomSeed = UUID.randomUUID().toString();

        String requestBody = """
        {
          "model": "gpt-4o-mini",
          "temperature": 0.9,
          "top_p": 1.0,
          "messages": [
            {
              "role": "system",
              "content": "You are a Vietnamese pronunciation tutor. You MUST respond ONLY with valid JSON. No extra text."
            },
            {
              "role": "user",
              "content": "Seed: %s. Generate a Vietnamese sentence that is %s difficulty. Each response MUST be unique and different from previous ones. Vary topic, vocabulary, and structure. Use topics like food, travel, emotions, daily life, school, work, and hobbies. Return ONLY valid JSON in this format: { \\"sentence\\": \\"Vietnamese sentence\\", \\"translation\\": \\"English translation\\" }"
            }
          ]
        }
        """.formatted(randomSeed, difficulty);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(response.body());

        // Check for error
        if (node.has("error")) {
            throw new RuntimeException("OpenAI Error: " + node.get("error").get("message").asText());
        }

        JsonNode choices = node.get("choices");
        if (choices == null || !choices.isArray() || choices.isEmpty()) {
            throw new RuntimeException("Invalid OpenAI response: " + response.body());
        }

        String content =  choices.get(0).get("message").get("content").asText();

        // Sometimes OpenAI will have extra characters in its response, handle accordingly
        content = content.replace("```json", "")
                .replace("```", "")
                .trim();

        JsonNode feedback = mapper.readTree(content);
        GenerationResult result = new GenerationResult();

        result.setActualSentence(feedback.get("sentence").asText());
        result.setTranslated(feedback.get("translation").asText());


        return result;
    }

    public GenerationResult generateUniqueSentence(String difficulty) throws Exception {
        GenerationResult result;

        do {
            result = generateSentence(difficulty);
        } while (redisService.isDuplicate(result.getActualSentence()));

        redisService.storeSentence(result.getActualSentence());
        return result;
    }
}
