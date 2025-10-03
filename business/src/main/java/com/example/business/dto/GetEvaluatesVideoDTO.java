package com.example.business.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetEvaluatesVideoDTO {
    private Long videoId;
    private Long likes;
    private Long dislikes;
}
