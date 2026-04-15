package com.brevityai.backend.practice;
import com.brevityai.backend.attempt.Attempt;
import com.brevityai.backend.attempt.AttemptRepository;
import com.brevityai.backend.llm.LLMService;
import com.brevityai.backend.practice.dto.EvaluationResult;
import com.brevityai.backend.practice.dto.GenerationResponse;
import com.brevityai.backend.practice.dto.GenerationResult;
import com.brevityai.backend.practice.dto.PracticeResponse;
import com.brevityai.backend.user.User;
import com.brevityai.backend.user.UserRepository;
import com.brevityai.backend.whisper.WhisperService;
import com.brevityai.backend.attempt.dto.AttemptResponse;
import lombok.*;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/practice")
@Data
@RequiredArgsConstructor
public class PracticeController {
    private final LLMService llmService;
    private final WhisperService whisperService;
    private final AttemptRepository attemptRepository;
    private final UserRepository userRepository;
    private GenerationResult generationResult;

    //Endpoint mainly for testing
    @GetMapping("/generate")
    public ResponseEntity<?> generateSentence(@RequestParam(defaultValue = "easy") String difficulty) {
        try {
            generationResult = llmService.generateUniqueSentence(difficulty);
            return ResponseEntity.ok(new GenerationResponse(generationResult.getActualSentence(), generationResult.getTranslated()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> practice (
            @RequestParam("file") MultipartFile file,
            @RequestParam(defaultValue = "easy") String difficulty
    ) {
        try {
            File tempFile = File.createTempFile("audio", ".m4a"); // may need to turn to .wav
            file.transferTo(tempFile);
            //GenerationResult expected = generationResult;

            // Get the current logged-in user
            String user = (String) Objects.requireNonNull(
                    SecurityContextHolder
                            .getContext()
                            .getAuthentication())
                            .getPrincipal();
            assert user != null;
            User currentUser = userRepository.findByUsername(user)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // send audio to Whisper and then transcript to LLM to evaluate
            String transcript = whisperService.transcribeAudio(tempFile);
            EvaluationResult feedback = llmService.evaluate(transcript, generationResult.getActualSentence());

            // Save to postgres db
            Attempt attempt = new Attempt();

            attempt.setUser(currentUser);
            attempt.setExpectedText(generationResult.getActualSentence());
            attempt.setTranscript(transcript);
            attempt.setScore(feedback.getScore());
            attempt.setAccuracy(feedback.getAccuracy());
            attempt.setMistakes(feedback.getMistakes());
            attempt.setSuggestion(feedback.getSuggestion());

            attemptRepository.save(attempt);
            System.out.println("Saved Attempt ID: " + attempt.getId());

            return ResponseEntity.ok(new PracticeResponse(transcript, generationResult.getActualSentence(),feedback));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/history")
    public ResponseEntity<?> getHistory() {
        try {
            // get current user
            String user = (String) Objects.requireNonNull(
                            SecurityContextHolder
                                    .getContext()
                                    .getAuthentication())
                                    .getPrincipal();
            assert user != null;
            User currentUser = userRepository.findByUsername(user)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            List<Attempt> attempts = attemptRepository.findByUser(currentUser);

            // store data in DTO and return
            List<AttemptResponse> response = attempts.stream()
                    .map(AttemptResponse::new)
                    .toList();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

}
