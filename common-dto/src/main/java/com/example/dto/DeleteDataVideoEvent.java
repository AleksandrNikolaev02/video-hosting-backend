package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeleteDataVideoEvent implements Serializable {
    private UUID videoId;
    private UUID previewId;
    private Long userId;
}
