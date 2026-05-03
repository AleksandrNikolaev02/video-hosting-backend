package com.example.business.mapper;

import com.example.business.model.User;
import com.example.dto.UserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mappings(value = {
            @Mapping(target = "id", source = "id"),
            @Mapping(target = "authId", source = "id"),
            @Mapping(target = "profile.fname", source = "fname"),
            @Mapping(target = "profile.sname", source = "sname"),
            @Mapping(target = "profile.email", source = "email")
    })
    User getUserFromUserDTO(UserDTO dto);
}
