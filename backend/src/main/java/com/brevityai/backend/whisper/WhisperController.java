package com.brevityai.backend.whisper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Map;

@RestController
@RequestMapping("/api/transcribe")
public class WhisperController {

    @Autowired
    private WhisperService whisperService;

    @PostMapping
    public ResponseEntity<?> transcribe(@RequestParam("file") MultipartFile file) throws Exception {
        var tempFile = File.createTempFile("audio", ".wav");
        file.transferTo(tempFile);

        String transcript = whisperService.transcribeAudio(tempFile);
        return ResponseEntity.ok(Map.of("userTranscript", transcript));
        //return whisperService.transcribeAudio(tempFile);
    }
}
