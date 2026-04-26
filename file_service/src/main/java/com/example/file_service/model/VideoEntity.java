package com.example.file_service.model;

import com.example.file_service.util.UuidV7Generator;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.UuidGenerator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

@Entity
@Table(name = "files")
@Getter
@Setter
@NoArgsConstructor
public class VideoEntity extends FileEntity {
    @Id
    @UuidGenerator(algorithm = UuidV7Generator.class)
    private UUID filename;
    @Column(name = "key")
    private String key;
    @Column(name = "business_id")
    private UUID businessId;
    @OneToMany(mappedBy = "file", cascade = CascadeType.ALL, orphanRemoval = true)
    @Fetch(value = FetchMode.SUBSELECT)
    @OrderBy(value = "partIndex ASC")
    private Collection<PartFile> parts = new ArrayList<>();
}
