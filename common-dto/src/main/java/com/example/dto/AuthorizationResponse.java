package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
@Setter
@Builder
@NoArgsConstructor
public class AuthorizationResponse {
    private Integer userId;
    private String role;
}
