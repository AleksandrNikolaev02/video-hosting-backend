package com.example.camunda.controller;

import com.example.camunda.config.ParametersConfig;
import com.example.camunda.dto.ExecutePipelineDTO;
import com.example.camunda.service.CamundaService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
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

    @PostMapping("/delete-channel-pipeline/{channelId}")
    public ResponseEntity<ExecutePipelineDTO> executeDeleteChannelPipeline(
            @RequestHeader("X-user-id") Long userId,
            @RequestHeader("X-user-role") String role,
            @PathVariable("channelId") Long channelId) {
        ExecutePipelineDTO dto = camundaService.startDeleteChannelPipeline(createParams(userId, role, channelId));

        return ResponseEntity.ok(dto);
    }

    private Map<String, Object> createParams(Long userId, String role, Long channelId) {
        return Map.of(parametersConfig.getUserId(), userId,
                      parametersConfig.getUserRole(), role,
                      parametersConfig.getNameChannelId(), channelId,
                      parametersConfig.getCountSuccessRequestName(), parametersConfig.getCountSuccessRequest(),
                      parametersConfig.getSuccessDeleteData(), false);
    }
}
