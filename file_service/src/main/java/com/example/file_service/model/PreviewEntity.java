package com.example.file_service.model;

import com.example.file_service.util.UuidV7Generator;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PreviewEntity extends FileEntity {
    @Id
    @UuidGenerator(algorithm = UuidV7Generator.class)
    private UUID filename;
}
