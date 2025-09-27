package com.example.file_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "part_files")
@Getter
@Setter
@NoArgsConstructor
public class PartFile {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "part_file_id_generator")
    @SequenceGenerator(name = "part_file_id_generator",
                       sequenceName = "part_file_id_generator",
                       allocationSize = 10)
    private Long id;
    @Column(name = "part_name")
    private String partName;
    @ManyToOne
    @JoinColumn(name = "file_id")
    private FileEntity file;
    @Column(name = "part_index")
    private int partIndex;
}
