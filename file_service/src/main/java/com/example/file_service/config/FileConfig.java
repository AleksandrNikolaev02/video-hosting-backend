package com.example.file_service.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
public class FileConfig {
    @Value("${file.dir:uploads}")
    private String bucket;
    @Value("${file.chunk-size}")
    private Long chunkSize;
    @Value("${file.preview-path}")
    private String previewPath;
    @Value("${file.video-path}")
    private String videoPath;
}

