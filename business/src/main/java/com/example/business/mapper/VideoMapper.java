package com.example.business.mapper;

import com.example.business.dto.CreateBaseVideoResponseDTO;
import com.example.business.dto.GetVideoDTO;
import com.example.business.model.Video;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VideoMapper {
    @Mapping(target = "title", source = "name")
    @Mapping(target = "video_preview", expression = """
            java(video.getPreview() != null ? (new com.example.business.dto.PreviewDTO(
                                         video.getPreview().getId(),
                                         video.getPreview().getPath())) : null)
            """)
    @Mapping(target = "tags", expression = """
            java(video.getTags().stream()
                                .map(com.example.business.model.Tag::getName)
                                .collect(java.util.stream.Collectors.toSet()))
            """)
    @Mapping(target = "countViewing", expression = "java(video.getViewings().size())")
    @Mapping(target = "userId", expression = "java(video.getCreator().getId())")
    @Mapping(target = "channelId", expression = "java(video.getChannel().getId())")
    @Mapping(target = "channelName", expression = "java(video.getChannel().getName())")
    @Mapping(target = "subscribersCount", expression = "java(video.getChannel().getCountSubs())")
    GetVideoDTO getVideoDtoFromVideo(Video video);

    @Mapping(target = "status", source = "videoStatus")
    @Mapping(target = "userId", expression = "java(video.getCreator().getId())")
    CreateBaseVideoResponseDTO getCreateVideoResponseDtoFromVideo(Video video);
}
