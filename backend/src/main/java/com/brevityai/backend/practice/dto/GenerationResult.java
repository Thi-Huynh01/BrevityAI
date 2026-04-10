package com.brevityai.backend.practice.dto;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenerationResult {

    //Can be used as a hint feature later on in dev
    private String translated;

    private String actualSentence;

}
