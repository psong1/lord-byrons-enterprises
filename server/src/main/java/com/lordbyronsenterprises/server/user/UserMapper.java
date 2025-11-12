package com.lordbyronsenterprises.server.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.BeanMapping;
import org.mapstruct.NullValuePropertyMappingStrategy;


@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toDto(User user);

    @Mapping(target = "password", ignore = true)
    User toEntity(CreateUserDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(UpdateUserDto dto, @MappingTarget User user);

//    public UserDto toDto(User user) {
//        UserDto dto = new UserDto();
//        dto.setId(user.getId());
//        dto.setFirstName(user.getFirstName());
//        dto.setLastName(user.getLastName());
//        dto.setEmail(user.getEmail());
//        dto.setUsername(user.getUsername());
//        dto.setRole(user.getRole());
//        return dto;
//    }

//    public User toEntity(UserDto dto) {
//        User user = new User();
//        user.setFirstName(dto.getFirstName());
//        user.setLastName(dto.getLastName());
//        user.setEmail(dto.getEmail());
//        user.setUsername(dto.getUsername());
//        user.setRole(dto.getRole());
//        return user;
//    }
}
