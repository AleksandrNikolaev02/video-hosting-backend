package com.example.email_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "codes")
@NoArgsConstructor
@AllArgsConstructor
public class TwoFactorCode {
    @Id
    @UuidGenerator
    private UUID id;

    @Column(name = "email")
    private String email;

    @Column(name = "code")
    private Integer code;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
