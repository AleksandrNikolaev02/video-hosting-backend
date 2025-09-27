package com.example.business.model;

import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "reactions")
@Getter
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
public class Reaction {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reaction_id_generator")
    @SequenceGenerator(name = "reaction_id_generator", sequenceName = "reaction_id_generator", allocationSize = 10)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "video_id", nullable = false)
    private Video video;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
