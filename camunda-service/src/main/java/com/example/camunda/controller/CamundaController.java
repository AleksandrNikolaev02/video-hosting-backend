package com.example.camunda.controller;

import com.example.camunda.dto.ExecutePipelineDTO;
import com.example.camunda.service.CamundaService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/camunda")
public class CamundaController {
    private final CamundaService camundaService;

    @PostMapping("/execute-pipeline")
    public ResponseEntity<ExecutePipelineDTO> executeDeleteChannelPipeline() {
        ExecutePipelineDTO dto = camundaService.startDeleteChannelPipeline();

        return ResponseEntity.ok(dto);
    }
}
