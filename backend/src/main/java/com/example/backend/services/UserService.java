package com.example.backend.services;


import java.util.List;

import org.springframework.stereotype.Service;

import com.example.backend.dto.user.UserRequest;
import com.example.backend.dto.user.UserResponse;
import com.example.backend.mapper.UserMap;
import com.example.backend.model.UserEntity;
import com.example.backend.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMap userMap;

    public UserService(UserRepository userRepository, UserMap userMap) {
        this.userRepository = userRepository;
        this.userMap = userMap;
    }

    public UserResponse createUser(UserRequest request){
        // 1. Convert DTO to Entity
        UserEntity userEntity = userMap.toEntity(request);
        // 2. Save Entity to DB
        UserEntity savedEntity = userRepository.save(userEntity);
        // 3. Convert Entity to Response DTO
        return userMap.toResponse(savedEntity);
    }
    public List<UserResponse> getAllUsers() {
        List<UserEntity> users = userRepository.findAll();
        return users.stream()
                .map(userMap::toResponse)
                .toList();
    }
    public UserResponse updateUser(Long id, UserRequest request){
        UserEntity userExsiting = userRepository.findById(id).orElseThrow();
        userExsiting.setUserName(request.getUsername());
        userExsiting.setPassword(request.getPassword());
        UserEntity updateUser = userRepository.save(userExsiting);
        return userMap.toResponse(updateUser);
    }
    public UserResponse deleteUser(Long id){
        UserEntity userExsit = userRepository.findById(id).orElseThrow();
        userExsit.setIsDeleted(true);
        userExsit.setDeletedBy(null);
        userRepository.save(userExsit);
        return userMap.toResponse(userExsit);
    }
}