package com.example.file_service.model;

import com.example.file_service.enums.FileStatus;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
    protected Long userId;
    @Column(name = "content_type")
    protected String contentType;
    @Column(name = "length")
    protected Long length;
    @Column(name = "filename_orig")
    protected String originalFilename;
    @Enumerated(value = EnumType.STRING)
    @Column(name = "status")
    protected FileStatus status;
}
