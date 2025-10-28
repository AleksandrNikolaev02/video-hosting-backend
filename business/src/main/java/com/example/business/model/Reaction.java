package com.example.business.model;

import com.example.business.dto.GetEvaluatesVideoDTO;
import jakarta.persistence.ColumnResult;
import jakarta.persistence.ConstructorResult;
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
import jakarta.persistence.SqlResultSetMapping;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "reactions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@SqlResultSetMapping(
        name = "getAllEvaluatesByVideo",
        classes = @ConstructorResult(
                targetClass = GetEvaluatesVideoDTO.class,
                columns = {
                        @ColumnResult(name = "likes"),
                        @ColumnResult(name = "dislikes"),
                        @ColumnResult(name = "videoId")
                }
        )
)
public class Reaction {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reaction_id_generator")
    @SequenceGenerator(name = "reaction_id_generator", sequenceName = "reaction_id_generator", allocationSize = 10)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "video_id", referencedColumnName = "filename")
    protected Video video;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    protected User user;
    @ManyToOne
    @JoinColumn(name = "comment_id")
    protected Comment comment;
}
