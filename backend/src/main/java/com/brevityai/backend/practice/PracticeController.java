package com.brevityai.backend.practice;
import com.brevityai.backend.attempt.Attempt;
import com.brevityai.backend.attempt.AttemptRepository;
import com.brevityai.backend.llm.LLMService;
import com.brevityai.backend.practice.dto.EvaluationResult;
import com.brevityai.backend.practice.dto.PracticeResponse;
import com.brevityai.backend.user.User;
import com.brevityai.backend.user.UserRepository;
import com.brevityai.backend.whisper.WhisperService;
import com.brevityai.backend.attempt.dto.AttemptResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/practice")
public class PracticeController {
    private final LLMService llmService;
    private final WhisperService whisperService;
    private final AttemptRepository attemptRepository;
    private final UserRepository userRepository;

    public PracticeController(LLMService llmService,
                              WhisperService whisperService,
                              AttemptRepository attemptRepository, UserRepository userRepository) {

        this.llmService = llmService;
        this.whisperService = whisperService;
        this.attemptRepository = attemptRepository;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<?> practice (
            @RequestParam("file") MultipartFile file,
            @RequestParam("expected") String expected
    ) {
        try {
            File tempFile = File.createTempFile("audio", ".m4a");
            file.transferTo(tempFile);

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
            EvaluationResult feedback = llmService.evaluate(transcript, expected);

            // Save to postgres db
            Attempt attempt = new Attempt();

            attempt.setUser(currentUser);
            attempt.setExpectedText(expected);
            attempt.setTranscript(transcript);
            attempt.setScore(feedback.getScore());
            attempt.setAccuracy(feedback.getAccuracy());
            attempt.setMistakes(feedback.getMistakes());
            attempt.setSuggestion(feedback.getSuggestion());

            attemptRepository.save(attempt);
            System.out.println("Saved Attempt ID: " + attempt.getId());

            return ResponseEntity.ok(new PracticeResponse(transcript, feedback));
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
