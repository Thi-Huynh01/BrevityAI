package com.brevityai.backend.practice.dto;

import lombok.Getter;

public class PracticeEvaluationResponse {

    @Getter
    private double score;

    @Getter
    private String feedback;

    public PracticeEvaluationResponse(double score, String feedback) {
        this.score = score;
        this.feedback = feedback;
    }
}
