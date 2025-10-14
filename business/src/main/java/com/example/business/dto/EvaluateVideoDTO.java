package com.example.business.dto;

import com.example.business.enums.EvaluateType;
import java.util.UUID;

public record EvaluateVideoDTO(
        UUID filename,
        EvaluateType evaluateType
) {
}
