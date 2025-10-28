package com.example.business.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChannelDTO {
    private Long id;
    private String name;
    private LocalDateTime createdAt;
    private Long authorId;
    private Integer countSubs;

}
