package com.example.backend.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.dto.UserRequestDTO;
import com.example.backend.dto.UserResponseDTO;
import com.example.backend.mapper.UserMapper;
import com.example.backend.model.UserEntity;
import com.example.backend.repository.EmployeeRepository;
import com.example.backend.repository.RoleRepository;
import com.example.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;

    @Transactional
    public UserResponseDTO createUser(UserRequestDTO request) {
        // Check if username already exists
        if (userRepository.existsByUserNameAndDeletedAtIsNull(request.getUserName())) {
            throw new RuntimeException("Username '" + request.getUserName() + "' already exists");
        }

        // Convert DTO to Entity
        UserEntity userEntity = userMapper.toEntity(request);

        // Link Employee
        if (request.getEmployeeId() != null) {
            userEntity.setEmployee(employeeRepository.findById(request.getEmployeeId())
                    .orElseThrow(() -> new RuntimeException("Employee not found")));
        }

        // Link Role
        if (request.getRoleId() != null) {
            userEntity.setRole(roleRepository.findById(request.getRoleId())
                    .orElseThrow(() -> new RuntimeException("Role not found")));
        }

        userEntity.setCreatedAt(LocalDateTime.now());
        userEntity.setUpdatedAt(LocalDateTime.now());

        // Save Entity to DB
        UserEntity savedEntity = userRepository.save(userEntity);

        // Convert Entity to Response DTO
        return userMapper.toResponseDTO(savedEntity);
    }

    public List<UserResponseDTO> getAllUsers() {
        List<UserEntity> users = userRepository.findAllByDeletedAtIsNull();
        return users.stream()
                .map(userMapper::toResponseDTO)
                .toList();
    }

    @Transactional
    public UserResponseDTO updateUser(Long id, UserRequestDTO request) {
        UserEntity userExisting = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));

        if (userExisting.getDeletedAt() != null) {
            throw new RuntimeException("Cannot update deleted user");
        }

        // Check if new username conflicts with existing ones
        if (!userExisting.getUserName().equals(request.getUserName()) &&
                userRepository.existsByUserNameAndDeletedAtIsNull(request.getUserName())) {
            throw new RuntimeException("Username '" + request.getUserName() + "' already exists");
        }

        userExisting.setUserName(request.getUserName());
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            userExisting.setPassword(request.getPassword());
        }

        // Link Employee
        if (request.getEmployeeId() != null) {
            userExisting.setEmployee(employeeRepository.findById(request.getEmployeeId())
                    .orElseThrow(() -> new RuntimeException("Employee not found")));
        }

        // Link Role
        if (request.getRoleId() != null) {
            userExisting.setRole(roleRepository.findById(request.getRoleId())
                    .orElseThrow(() -> new RuntimeException("Role not found")));
        }

        userExisting.setUpdatedAt(LocalDateTime.now());

        UserEntity updatedUser = userRepository.save(userExisting);
        return userMapper.toResponseDTO(updatedUser);
    }

    @Transactional
    public void deleteUser(Long id) {
        UserEntity userExist = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));

        // Soft delete
        userExist.setDeletedAt(LocalDateTime.now());
        userExist.setUpdatedAt(LocalDateTime.now());
        userRepository.save(userExist);
    }

    public UserResponseDTO getUserById(Long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));

        if (user.getDeletedAt() != null) {
            throw new RuntimeException("User has been deleted");
        }

        return userMapper.toResponseDTO(user);
    }
}