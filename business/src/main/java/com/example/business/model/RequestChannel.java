package com.example.business.model;

import com.example.business.enums.RequestChannelStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
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
@Table(name = "requests_channels")
public class RequestChannel {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "requests_channels_id_generator")
    @SequenceGenerator(name = "requests_channels_id_generator",
                       sequenceName = "requests_channels_id_generator",
                       allocationSize = 10)
    private Long id;
    @Column(name = "status")
    @Enumerated(value = EnumType.STRING)
    private RequestChannelStatus status;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @ManyToOne
    @JoinColumn(name = "creator_id")
    private User creator;
    @ManyToOne
    @JoinColumn(name = "channel_id")
    private Channel channel;
}
