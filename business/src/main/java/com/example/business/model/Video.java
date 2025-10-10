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
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "videos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE,
                           CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(
            name = "videos_tags",
            joinColumns = @JoinColumn(name = "video_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id", referencedColumnName = "id")
    )
    private Set<Tag> tags = new HashSet<>();
}
