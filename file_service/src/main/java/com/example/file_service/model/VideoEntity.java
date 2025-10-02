package com.example.file_service.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.ArrayList;
import java.util.Collection;

@Entity
@Table(name = "files")
@Getter
@Setter
@NoArgsConstructor
public class VideoEntity extends FileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "file_id_generator")
    @SequenceGenerator(name = "file_id_generator",
                       sequenceName = "file_id_generator",
                       allocationSize = 10)
    private Long id;
    @Column(name = "key")
    private String key;
    @OneToMany(mappedBy = "file", cascade = CascadeType.ALL, orphanRemoval = true)
    @Fetch(value = FetchMode.SUBSELECT)
    @OrderBy(value = "partIndex ASC")
    private Collection<PartFile> parts = new ArrayList<>();
}
