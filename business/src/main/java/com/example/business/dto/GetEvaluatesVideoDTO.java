package com.example.business.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetEvaluatesVideoDTO {
    private UUID videoId;
    private Long likes;
    private Long dislikes;
}
