package com.example.business.dto;

import com.example.business.enums.EvaluateType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EvaluateVideoDTO {
    private Long videoId;
    private EvaluateType evaluateType;
}
