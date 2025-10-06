package com.example.business.mapper;

import com.example.business.dto.CreateBaseVideoResponseDTO;
import com.example.business.dto.GetVideoDTO;
import com.example.business.model.Video;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VideoMapper {
    @Mapping(target = "title", source = "name")
    @Mapping(target = "dto", expression = """
            java(video.getPreview() != null ? (new com.example.business.dto.PreviewDTO(
                                         video.getPreview().getId(),
                                         video.getPreview().getPath())) : null)
            """)
    GetVideoDTO getVideoDtoFromVideo(Video video);

    @Mapping(target = "status", source = "videoStatus")
    @Mapping(target = "userId", expression = "java(video.getCreator().getId())")
    CreateBaseVideoResponseDTO getCreateVideoResponseDtoFromVideo(Video video);
}
