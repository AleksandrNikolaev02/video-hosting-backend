package com.example.business.mapper;

import com.example.business.dto.ChannelDTO;
import com.example.business.dto.GetAllRequestsDTO;
import com.example.business.dto.GetBlockedChannelDTO;
import com.example.business.model.BlockedChannel;
import com.example.business.model.Channel;
import com.example.business.model.RequestChannel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ChannelMapper {
    @Mapping(target = "requestId", source = "id")
    @Mapping(target = "channelId", expression = "java(request.getChannel().getId())")
    GetAllRequestsDTO getAllRequestsDtoFromRequestChannel(RequestChannel request);

    @Mapping(target = "channelId", source = "id")
    GetBlockedChannelDTO getBlockedChannelDtoFromBlockedChannel(BlockedChannel channel);

    @Mapping(target = "authorId", expression = "java(channel.getAuthor().getId())")
    ChannelDTO getChannelDtoFromChannel(Channel channel);
}
