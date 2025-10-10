package com.example.business.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "viewings", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"userAgent", "ip"}),
        @UniqueConstraint(columnNames = "userId")
})
public class Viewing {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "viewing_id_generator")
    @SequenceGenerator(name = "viewing_id_generator",
                       sequenceName = "viewing_id_generator",
                       allocationSize = 10)
    private Long id;
    @Column(name = "user_agent")
    private String userAgent;
    @Column(name = "ip")
    private String ip;
    @Column(name = "user_id")
    private Long userId;
    @ManyToOne
    @JoinColumn(name = "video_id")
    private Video video;
}
