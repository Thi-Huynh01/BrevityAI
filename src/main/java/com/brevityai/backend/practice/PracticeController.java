package com.brevityai.backend.practice;
import com.brevityai.backend.llm.LLMService;
import com.brevityai.backend.practice.dto.EvaluationResult;
import com.brevityai.backend.practice.dto.PracticeResponse;
import com.brevityai.backend.whisper.WhisperService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Map;

@RestController
@RequestMapping("/api/practice")
public class PracticeController {
    //private final PracticeService practiceService;
    private final LLMService llmService;
    private final WhisperService whisperService;

    public PracticeController(LLMService llmService, WhisperService whisperService) {

        //this.practiceService = practiceService;
        this.llmService = llmService;
        this.whisperService = whisperService;
    }

    @PostMapping
    public ResponseEntity<?> practice (
            @RequestParam("file") MultipartFile file,
            @RequestParam("expected") String expected
    ) {
        try {
            File tempFile = File.createTempFile("audio", ".wav");
            file.transferTo(tempFile);

            String transcript = whisperService.transcribeAudio(tempFile);
            EvaluationResult feedback = llmService.evaluate(transcript, expected);

            return ResponseEntity.ok(new PracticeResponse(transcript, feedback));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

//    @PostMapping("/evaluate")
//    public ResponseEntity<PracticeEvaluationResponse> evaluate(
//           @Valid @RequestBody PracticeEvaluationRequest request
//            ) {
//        PracticeEvaluationResponse result =
//                practiceService.evaluate(request.getLanguage(), request.getExpectedText(), request.getUserTranscript());
//        return ResponseEntity.ok(result);
//    }
//
//    @GetMapping("/prompt")
//    public PracticePromptResponse getPrompt(
//            @RequestParam(defaultValue = "vi") String language,
//            @RequestParam(defaultValue = "easy") String difficulty
//    ) {
//        return practiceService.generatePrompt(language,difficulty);
//    }
//
//    @PostMapping(value="/transcribe", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public Map<String, String> transcribe(@RequestParam("audio") MultipartFile audioFile) {
//        // TODO: real speech to text later
//
//        return Map.of("transcript", "Xin chào, hôm nay bạn thế nào?");
//    }
}
