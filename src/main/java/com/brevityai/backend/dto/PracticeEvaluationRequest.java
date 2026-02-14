package com.brevityai.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

public class PracticeEvaluationRequest {

    @Setter
    @Getter
    @NotBlank
    private String language;

    @Setter
    @Getter
    @NotBlank
    private String expectedText;

    @Setter
    @Getter
    @NotBlank
    private String userTranscript;




}
