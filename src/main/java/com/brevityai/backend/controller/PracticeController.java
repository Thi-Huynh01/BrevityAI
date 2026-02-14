package com.brevityai.backend.controller;
import com.brevityai.backend.dto.PracticeEvaluationRequest;
import com.brevityai.backend.dto.PracticeEvaluationResponse;
import com.brevityai.backend.dto.PracticePromptResponse;
import com.brevityai.backend.service.PracticeService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/practice")
public class PracticeController {
    private final PracticeService practiceService;

    public PracticeController(PracticeService practiceService) {
        this.practiceService = practiceService;
    }

    @PostMapping("/evaluate")
    public ResponseEntity<PracticeEvaluationResponse> evaluate(
           @Valid @RequestBody PracticeEvaluationRequest request
            ) {
        PracticeEvaluationResponse result =
                practiceService.evaluate(request.getLanguage(), request.getExpectedText(), request.getUserTranscript());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/prompt")
    public PracticePromptResponse getPrompt(
            @RequestParam(defaultValue = "vi") String language,
            @RequestParam(defaultValue = "easy") String difficulty
    ) {
        return practiceService.generatePrompt(language,difficulty);
    }

    @PostMapping(value="/transcribe", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, String> transcribe(@RequestParam("audio") MultipartFile audioFile) {
        // TODO: real speech to text later

        return Map.of("transcript", "Xin chào, hôm nay bạn thế nào?");
    }
}
