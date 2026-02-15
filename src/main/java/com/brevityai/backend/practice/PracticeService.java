package com.brevityai.backend.practice;

import com.brevityai.backend.practice.dto.PracticeEvaluationResponse;
import com.brevityai.backend.practice.dto.PracticePromptResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class PracticeService {

    private final Random random = new Random();

    private final Map<String, List<String>> vietnamesePrompts = Map.of(
            "easy", List.of(
                    "Xin chào",
                    "Tôi tên là Minh",
                    "Tôi thích cà phê",
                    "Hôm nay trời đẹp",
                    "Bạn khỏe không?"
            ),
            "medium", List.of(
                    "Hôm nay bạn cảm thấy thế nào?",
                    "Tôi đang học tiếng Việt",
                    "Cuối tuần bạn thường làm gì?",
                    "Tôi thích nghe nhạc khi rảnh",
                    "Bạn có thể giúp tôi không?"
            ),
            "hard", List.of(
                    "Tôi muốn cải thiện khả năng phát âm tiếng Việt của mình",
                    "Học một ngôn ngữ mới đòi hỏi rất nhiều kiên nhẫn",
                    "Bạn có thể giới thiệu cho tôi một quán ăn ngon không?",
                    "Khi có thời gian rảnh, tôi thường đọc sách hoặc nghe podcast",
                    "Việc luyện nói mỗi ngày giúp tiến bộ nhanh hơn"
            )
    );

    public PracticePromptResponse generatePrompt(String language, String difficulty) {
        if (!"vi".equalsIgnoreCase(language)) {
            return new PracticePromptResponse("Language not supported yet", difficulty, language);
        }
        List<String> prompts = vietnamesePrompts.getOrDefault(difficulty, vietnamesePrompts.get("easy"));
        String sentence = prompts.get(random.nextInt(prompts.size()));

        return new PracticePromptResponse(sentence, difficulty, language);
    }

    public PracticeEvaluationResponse evaluate(String language, String expected, String userTranscript) {
        // TODO: Replace with AI Model later
//        double fakeScore = Math.min(1.0, text.length() / 50.0);
//        String fakeFeedback = "Good Attempt! Try speaking more clearly.";
//
//        return new PracticeEvaluationResponse(fakeScore, fakeFeedback);

        double score = getScore(expected, userTranscript);

        String feedback;
        if (score > 0.8) {
            feedback = "Great Job! Your pronunciation is pretty clear.";
        } else if (score > 0.5) {
            feedback = "Good Attempt! Try to pronounce each word more clearly.";
        } else {
            feedback = "Keep practicing. Focus on speaking slowly and clearly.";
        }

        return new PracticeEvaluationResponse(score, feedback);

    }

    private static double getScore(String expected, String userTranscript) {
        String normExpected = expected.toLowerCase().replaceAll("[^a-zà-ỹ\\s]", "");
        String normActual = userTranscript.toLowerCase().replaceAll("[^a-zà-ỹ\\s]", "");

        String[] expectedTokens = normExpected.split("\\s+");
        String[] actualTokens = normActual.split("\\s+");

        int matches = 0;

        for (String token : expectedTokens) {
            for (String actualToken : actualTokens) {
                if (token.equals(actualToken)) {
                    matches ++;
                    break;
                }
            }
        }

        return (double) matches /expectedTokens.length;
    }
}
