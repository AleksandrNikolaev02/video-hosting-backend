package com.example.file_service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SaveChunkDTO {
    @Positive private String key;
    @NotNull private String filename; // оригинальное имя файла
    @NotNull private String contentType;
    @NotNull private Integer partIndex;
}
