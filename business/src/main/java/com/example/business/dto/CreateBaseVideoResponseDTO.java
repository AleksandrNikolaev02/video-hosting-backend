package com.example.business.dto;

import com.example.business.enums.VideoStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateBaseVideoResponseDTO {
    private Long id;
    private Long userId;
    private VideoStatus status;
}
