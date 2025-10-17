package com.example.camunda.controller;

import com.example.camunda.config.ParametersConfig;
import com.example.camunda.dto.ExecutePipelineDTO;
import com.example.camunda.service.CamundaService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/camunda")
public class CamundaController {
    private final CamundaService camundaService;
    private final ParametersConfig parametersConfig;

    @PostMapping("/execute-pipeline")
    public ResponseEntity<ExecutePipelineDTO> executeDeleteChannelPipeline(
            @RequestHeader("X-user-id") Long userId,
            @RequestHeader("X-user-role") String role) {
        ExecutePipelineDTO dto = camundaService.startDeleteChannelPipeline(createParams(userId, role));

        return ResponseEntity.ok(dto);
    }

    private Map<String, Object> createParams(Long userId, String role) {
        return Map.of(parametersConfig.getUserId(), userId,
                      parametersConfig.getUserRole(), role);
    }
}
