package com.example.business.model;

import com.example.business.enums.VideoStatus;
import com.example.business.util.Evaluatable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "videos")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Video implements Evaluatable {
    @Id
    @UuidGenerator
    @Column(name = "filename", unique = true, nullable = false)
    private UUID filename;
    @BatchSize(size = 1000)
    @OneToMany(mappedBy = "video", cascade = CascadeType.ALL, orphanRemoval = true)
    private Collection<Like> likes = new ArrayList<>();
    @BatchSize(size = 1000)
    @OneToMany(mappedBy = "video", cascade = CascadeType.ALL, orphanRemoval = true)
    private Collection<Dislike> dislikes = new ArrayList<>();
    @Column(name = "description")
    private String description;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User creator;
    @Column(name = "name")
    private String name;
    @Column(name = "status")
    @Enumerated(value = EnumType.STRING)
    private VideoStatus videoStatus;
    @Column(name = "created_at")
    private LocalDateTime date;
    @OneToOne(mappedBy = "video", orphanRemoval = true, cascade = CascadeType.ALL)
    private Preview preview;
    @ManyToOne
    @JoinColumn(name = "playlist_id")
    private Playlist playlist;
    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(
            name = "videos_tags",
            joinColumns = @JoinColumn(name = "video_id", referencedColumnName = "filename"),
            inverseJoinColumns = @JoinColumn(name = "tag_id", referencedColumnName = "id")
    )
    private Set<Tag> tags = new HashSet<>();
    @BatchSize(size = 1000)
    @OneToMany(mappedBy = "video", cascade = CascadeType.ALL, orphanRemoval = true)
    private Collection<Viewing> viewings = new ArrayList<>();
    @ManyToOne
    @JoinColumn(name = "channel_id", referencedColumnName = "id")
    private Channel channel;
}
