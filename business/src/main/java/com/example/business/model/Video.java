package com.example.business.model;

import com.example.business.enums.VideoStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@Table(name = "videos")
@NoArgsConstructor
@Getter
@Setter
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "video_id_generator")
    @SequenceGenerator(name = "video_id_generator", sequenceName = "video_id_generator", allocationSize = 10)
    private Long id;
    @OneToMany(mappedBy = "video", cascade = CascadeType.ALL, orphanRemoval = true)
    private Collection<Like> likes = new ArrayList<>();
    @OneToMany(mappedBy = "video", cascade = CascadeType.ALL, orphanRemoval = true)
    private Collection<Dislike> dislikes = new ArrayList<>();
    @Column(name = "description")
    private String description;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User creator;
    @Column(name = "path", unique = true)
    private String path;
    @Column(name = "name")
    private String name;
    @Column(name = "status")
    @Enumerated(value = EnumType.STRING)
    private VideoStatus videoStatus;
    @Column(name = "created_at")
    private LocalDateTime date;
    @OneToOne(mappedBy = "video", orphanRemoval = true, cascade = CascadeType.ALL)
    private Preview preview;
}
