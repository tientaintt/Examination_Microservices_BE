package com.spring.boot.identity_service.mapper;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.spring.boot.identity_service.dto.response.UserResponse;
import com.spring.boot.identity_service.entity.Role;
import com.spring.boot.identity_service.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(source = "username", target = "loginName")
    UserResponse toUserResponse(User user);
    default List<String> mapRoles(Set<Role> roles) {
        return roles.stream()
                .map(Role::getName)
                .collect(Collectors.toList());
    }
}
