package com.brevityai.backend.practice.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PracticeResponse {
    private String transcript;
    private EvaluationResult evaluation;
}
