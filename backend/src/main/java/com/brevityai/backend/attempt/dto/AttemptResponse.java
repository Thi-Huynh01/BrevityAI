package com.brevityai.backend.attempt.dto;

import com.brevityai.backend.attempt.Attempt;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class AttemptResponse {
    private String transcript;
    private int score;
    private String accuracy;
    private String mistakes;
    private String suggestion;

    public AttemptResponse(Attempt attempt){
        this.transcript = attempt.getTranscript();
        this.score = attempt.getScore();
        this.accuracy = attempt.getAccuracy();
        this.mistakes = attempt.getMistakes();
        this.suggestion = attempt.getSuggestion();
    }
}
