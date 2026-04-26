package com.example.business.mapper;

import com.example.business.dto.GetPlaylistsDTO;
import com.example.business.model.Playlist;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PlaylistMapper {
    @Mapping(target = "title", source = "name")
    GetPlaylistsDTO getPlaylistFromPlaylist(Playlist playlist);
}
