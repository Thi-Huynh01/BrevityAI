package com.brevityai.backend.practice.dto;

import lombok.Getter;

public class PracticePromptResponse {

    @Getter
    private String sentence;

    @Getter
    private String difficulty;

    @Getter
    private String language;

    public PracticePromptResponse(String sentence, String difficulty, String language) {
        this.sentence = sentence;
        this.difficulty = difficulty;
        this.language = language;
    }

}
