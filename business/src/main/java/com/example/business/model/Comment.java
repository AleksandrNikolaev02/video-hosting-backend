package com.example.business.model;

import com.example.business.util.Evaluatable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "comments")
public class Comment implements Evaluatable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "comment_id_generator")
    @SequenceGenerator(name = "comment_id_generator", sequenceName = "comment_id_generator", allocationSize = 10)
    private Long id;
    @Column(name = "content")
    private String content;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User creator;
    @ManyToOne
    @JoinColumn(name = "video_id", nullable = false, referencedColumnName = "filename")
    private Video video;
    @Column(name = "is_edit")
    private boolean isEdit;
    @ManyToOne
    @JoinColumn(name = "comment_id")
    private Comment parent;
    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL,
               orphanRemoval = true)
    private Collection<Like> likes = new ArrayList<>();
    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL,
               orphanRemoval = true)
    private Collection<Dislike> dislikes = new ArrayList<>();
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;
}
