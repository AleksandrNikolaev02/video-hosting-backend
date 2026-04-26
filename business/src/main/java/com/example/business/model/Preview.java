package com.example.business.model;

import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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
    @UuidGenerator
    private UUID id;
    @Column(name = "path")
    private String path;
    @OneToOne
    @JoinColumn(name = "video_id", nullable = false, referencedColumnName = "filename")
    private Video video;
}
