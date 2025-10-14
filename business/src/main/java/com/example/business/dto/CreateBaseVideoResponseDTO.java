package com.example.business.dto;

import com.example.business.enums.VideoStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateBaseVideoResponseDTO {
    private UUID filename;
    private Long userId;
    private VideoStatus status;
}
