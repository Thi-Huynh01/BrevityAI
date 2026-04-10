package com.brevityai.backend.practice.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenerationResponse {

    private String sentence;
    private String translation;
}
