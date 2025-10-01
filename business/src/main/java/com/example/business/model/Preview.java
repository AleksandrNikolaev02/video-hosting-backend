package com.example.business.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "previews")
@Getter
@Setter
@NoArgsConstructor
public class Preview {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "previews-id-generator")
    @SequenceGenerator(name = "previews-id-generator",
                       sequenceName = "previews-id-generator",
                       allocationSize = 10)
    private Long id;
    @Column(name = "path")
    private String path;
    @OneToOne(orphanRemoval = true)
    @JoinColumn(name = "video_id", nullable = false)
    private Video video;
}
