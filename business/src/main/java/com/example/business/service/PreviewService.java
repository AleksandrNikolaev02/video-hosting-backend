package com.example.business.service;

import com.example.business.repository.PreviewRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PreviewService {
    private final PreviewRepository previewRepository;

}
