package com.example.backend.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.dto.user.UserRequest;
import com.example.backend.dto.user.UserResponse;
import com.example.backend.mapper.UserMap;
import com.example.backend.model.EmployeeEntity;
import com.example.backend.model.RoleEntity;
import com.example.backend.model.UserEntity;
import com.example.backend.repository.EmployeeRepository;
import com.example.backend.repository.RoleRepository;
import com.example.backend.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final RoleRepository roleRepository;
    private final UserMap userMap;

    public UserService(UserRepository userRepository, EmployeeRepository employeeRepository,
            RoleRepository roleRepository, UserMap userMap) {
        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
        this.roleRepository = roleRepository;
        this.userMap = userMap;
    }

    @Transactional
    public UserResponse createUser(UserRequest request) {
        // Validate input
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new RuntimeException("Username is required");
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new RuntimeException("Password is required");
        }
        if (request.getEmployee() == null) {
            throw new RuntimeException("Employee ID is required");
        }
        if (request.getRole() == null) {
            throw new RuntimeException("Role ID is required");
        }

        // Check if username already exists
        if (userRepository.existsByUserNameAndDeletedAtIsNull(request.getUsername())) {
            throw new RuntimeException("Username '" + request.getUsername() + "' already exists");
        }

        try {
            // Fetch Employee and Role entities by their IDs
            EmployeeEntity employee = employeeRepository.findById(request.getEmployee())
                    .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + request.getEmployee()));

            RoleEntity role = roleRepository.findById(request.getRole())
                    .orElseThrow(() -> new RuntimeException("Role not found with ID: " + request.getRole()));

            // Convert DTO to Entity
            UserEntity userEntity = userMap.toEntity(request);
            userEntity.setEmployee(employee);
            userEntity.setRole(role);
            userEntity.setCreatedAt(LocalDateTime.now());
            userEntity.setUpdatedAt(LocalDateTime.now());

            // Save Entity to DB
            UserEntity savedEntity = userRepository.save(userEntity);

            // Convert Entity to Response DTO
            return userMap.toResponse(savedEntity);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create user: " + e.getMessage(), e);
        }
    }

    public List<UserResponse> getAllUsers() {
        List<UserEntity> users = userRepository.findAllByDeletedAtIsNull();
        return users.stream()
                .map(userMap::toResponse)
                .toList();
    }

    @Transactional
    public UserResponse updateUser(Long id, UserRequest request) {
        UserEntity userExisting = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));

        if (userExisting.getDeletedAt() != null) {
            throw new RuntimeException("Cannot update deleted user");
        }

        // Check if new username conflicts with existing ones
        if (!userExisting.getUserName().equals(request.getUsername()) &&
                userRepository.existsByUserNameAndDeletedAtIsNull(request.getUsername())) {
            throw new RuntimeException("Username '" + request.getUsername() + "' already exists");
        }

        userExisting.setUserName(request.getUsername());
        userExisting.setPassword(request.getPassword());
        userExisting.setUpdatedAt(LocalDateTime.now());

        UserEntity updatedUser = userRepository.save(userExisting);
        return userMap.toResponse(updatedUser);
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

    /**
     * Get user by ID
     */
    public UserResponse getUserById(Long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));

        if (user.getDeletedAt() != null) {
            throw new RuntimeException("User has been deleted");
        }

        return userMap.toResponse(user);
    }
}