package com.example.business.dto;

import com.example.business.enums.VideoStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetVideoDTO {
    private String description;
    private String title;
    private String path;
    private VideoStatus videoStatus;
    private LocalDateTime date;
}
