package com.example.file_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SaveChunkDTO {
    @NotBlank private String key;
    @NotNull private String filename; // оригинальное имя файла
    @NotNull private String contentType;
    @NotNull private Integer partIndex;
}
