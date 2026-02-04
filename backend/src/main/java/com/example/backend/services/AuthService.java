package com.example.backend.services;

import com.example.backend.dto.user.LoginRequest;
import com.example.backend.dto.user.LoginResponse;
import com.example.backend.model.UserEntity;
import com.example.backend.repository.UserRepository;
import com.example.backend.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;

    public LoginResponse login(LoginRequest request) {
        // 1. Authenticate using Spring Security
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 2. Generate Real JWT Token
        String jwt = jwtUtils.generateJwtToken(authentication);

        // 3. Get User Details
        UserEntity user = userRepository.findByUserNameAndDeletedAtIsNull(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found after authentication"));

        // 4. Build Response
        LoginResponse response = new LoginResponse();
        response.setUserId(user.getUserId());
        response.setUsername(user.getUserName());
        response.setToken(jwt);
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
