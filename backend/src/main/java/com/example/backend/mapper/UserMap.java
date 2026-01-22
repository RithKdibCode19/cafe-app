package com.example.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.backend.dto.user.UserRequest;
import com.example.backend.dto.user.UserResponse;
import com.example.backend.model.UserEntity;

@Mapper(componentModel = "spring")
public interface UserMap {
    // convert request DTO to entity
    @Mapping(target = "userId",ignore = true)
    @Mapping(target="userName",  source="username")
    @Mapping(target="password",  source="password")
    @Mapping(target="createdAt", ignore = true)
    @Mapping(target="updatedAt", ignore = true)
    @Mapping(target="deletedAt", ignore = true)
    @Mapping(target="isDeleted", ignore = true)
    @Mapping(target="deletedBy", ignore = true)
    @Mapping(target="isActive", ignore = true)
    UserEntity toEntity(UserRequest request);
    UserResponse toResponse(UserEntity entity);
}
