package com.brevityai.backend.practice.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EvaluationResult {
    private int score;
    private String accuracy;
    private String mistakes;
    private String suggestion;
}
