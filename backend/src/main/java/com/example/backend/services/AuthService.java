package com.example.backend.services;

import com.example.backend.dto.user.LoginRequest;
import com.example.backend.dto.user.LoginResponse;
import com.example.backend.model.UserEntity;
import com.example.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.UUID; // Mock token

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    public LoginResponse login(LoginRequest request) {
        // 1. Find User
        UserEntity user = userRepository.findByUserNameAndDeletedAtIsNull(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        // 2. Validate Password (Simple check for now, use BCrypt in prod)
        if (!user.getPassword().equals(request.getPassword())) {
             throw new RuntimeException("Invalid username or password");
        }

        // 3. Generate Token (Mock for now)
        String token = UUID.randomUUID().toString();

        // 4. Build Response
        LoginResponse response = new LoginResponse();
        response.setUserId(user.getUserId());
        response.setUsername(user.getUserName());
        response.setToken(token);
        response.setRoleName(user.getRole() != null ? user.getRole().getRoleName() : "UNKNOWN");
        
        if (user.getEmployee() != null) {
            response.setEmployeeName(user.getEmployee().getFullName());
            if (user.getEmployee().getBranch() != null) {
                response.setBranchId(user.getEmployee().getBranch().getBranchId());
                response.setBranchName(user.getEmployee().getBranch().getName());
            }
        }

        return response;
    }
}
