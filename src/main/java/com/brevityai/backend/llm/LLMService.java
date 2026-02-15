package com.brevityai.backend.llm;

import com.brevityai.backend.practice.dto.EvaluationResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class LLMService {

    private final String apiKey = System.getenv("OPENAI_API_KEY");

    public EvaluationResult evaluate(String transcript, String expected) throws Exception {
        String requestBody = """
        {
          "model": "gpt-4o-mini",
          "messages": [
            {
              "role": "system",
              "content": "You are a Vietnamese pronunciation tutor."
            },
            {
              "role": "user",
              "content": "User said: %s. Expected: %s. Return ONLY valid JSON in this format: { \\"score\\": number (0-100), \\"accuracy\\": \\"short description\\", \\"mistakes\\": \\"what was wrong\\", \\"suggestion\\": \\"how to improve\\" }"
            }
          ]
        }
        """.formatted(transcript, expected);

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

        // Safe extraction
        JsonNode choices = node.get("choices");
        if (choices == null || !choices.isArray() || choices.isEmpty()) {
            throw new RuntimeException("Invalid OpenAI response: " + response.body());
        }

        String content =  choices.get(0).get("message").get("content").asText();
        content = content.replace("```json", "")
                .replace("```", "")
                .trim();

        JsonNode feedback = mapper.readTree(content);
        EvaluationResult result = new EvaluationResult();

        result.setScore(feedback.get("score").asInt());
        result.setMistakes(feedback.get("mistakes").asText());
        result.setAccuracy(feedback.get("accuracy").asText());
        result.setSuggestion(feedback.get("suggestion").asText());

        return result;
    }
}
