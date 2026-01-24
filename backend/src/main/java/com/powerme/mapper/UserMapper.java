package com.powerme.mapper;

import com.powerme.dto.UserDto;
import com.powerme.dto.UserRegisterDto;
import com.powerme.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface UserMapper {

    UserDto toDto(User entity);

    // Pour l'inscription
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toEntity(UserRegisterDto dto);
}
