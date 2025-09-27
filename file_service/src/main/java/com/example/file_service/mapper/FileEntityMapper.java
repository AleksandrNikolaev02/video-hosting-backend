package com.example.file_service.mapper;

import com.example.file_service.dto.FileEntityDTO;
import com.example.file_service.model.FileEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FileEntityMapper {
    @Mapping(target = "id", source = "id")
    @Mapping(target = "filename", source = "filename")
    @Mapping(target = "contentType", source = "contentType")
    FileEntityDTO getFileEntityDTOFromFileEntity(FileEntity file);
}
