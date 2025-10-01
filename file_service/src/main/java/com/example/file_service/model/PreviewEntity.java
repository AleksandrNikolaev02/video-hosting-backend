package com.example.file_service.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PreviewEntity extends FileEntity {
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "images-id-generator")
    @SequenceGenerator(name = "images-id-generator",
                       sequenceName = "images-id-generator",
                       allocationSize = 10)
    private Long id;
}
