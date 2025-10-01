package com.example.file_service.mapper;

import com.example.dto.CreateVideoDTO;
import com.example.file_service.dto.FileEntityDTO;
import com.example.file_service.model.VideoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FileEntityMapper {
    @Mapping(target = "id", source = "id")
    @Mapping(target = "filename", source = "filename")
    @Mapping(target = "contentType", source = "contentType")
    FileEntityDTO getFileEntityDTOFromFileEntity(VideoEntity file);

    @Mapping(target = "path", expression = "java(video.getFilename())")
    @Mapping(target = "userId", expression = "java(video.getUserId())")
    CreateVideoDTO getCreateVideoDtoFromVideoEntity(VideoEntity video);
}
