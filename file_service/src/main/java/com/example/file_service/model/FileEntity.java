package com.example.file_service.model;

import com.example.file_service.enums.FileStatus;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@MappedSuperclass
@NoArgsConstructor
@Getter
@Setter
public class FileEntity {
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "content_type")
    private String contentType;
    @Column(name = "length")
    private Long length;
    @Column(name = "filename_orig")
    private String originalFilename;
    @Column(name = "status")
    private FileStatus status;
}
