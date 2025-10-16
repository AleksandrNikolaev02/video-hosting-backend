package com.example.business.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "blocked_channels")
public class BlockedChannel {
    @Id
    private Long id;
    @Column(name = "time_block")
    private LocalDateTime timeBlock;
    @Column(name = "message")
    private String message;
    @MapsId
    @OneToOne
    @JoinColumn(name = "id", unique = true)
    private Channel channel;
}
