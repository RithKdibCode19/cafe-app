package com.example.backend.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    
    private Long userId;
    private String username;
    private String token; // JWT token
    private String roleName;
    private String employeeName;
    private Long branchId;
    private String branchName;
}
