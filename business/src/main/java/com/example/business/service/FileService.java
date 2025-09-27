package com.example.business.service;

import com.example.dto.VideoLoadDTO;
import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class FileService {
    private final KafkaTemplate<String, VideoLoadDTO> kafkaTemplate;
}
