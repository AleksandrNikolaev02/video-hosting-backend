package com.example.business.mapper;

import com.example.business.model.Tag;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TagMapper {
    @Mapping(target = "name", expression = "java(name)")
    Tag getTagFromTagDto(String name);
}
