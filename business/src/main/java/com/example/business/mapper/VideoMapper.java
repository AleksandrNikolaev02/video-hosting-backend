package com.example.business.mapper;

import com.example.business.dto.GetVideoDTO;
import com.example.business.model.Video;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VideoMapper {
    @Mapping(target = "title", source = "name")
    GetVideoDTO getVideoDtoFromVideo(Video video);
}
