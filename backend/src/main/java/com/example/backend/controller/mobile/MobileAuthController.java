package com.example.backend.controller.mobile;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.dto.mobile.MobileOrderDTO;
import com.example.backend.security.JwtUtils;
import com.example.backend.services.mobile.MobileAuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/mobile/auth")
@Tag(name = "Mobile - Auth", description = "Customer authentication endpoints")
public class MobileAuthController {

    private final MobileAuthService mobileAuthService;
    private final JwtUtils jwtUtils;

    public MobileAuthController(MobileAuthService mobileAuthService, JwtUtils jwtUtils) {
        this.mobileAuthService = mobileAuthService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/register")
    @Operation(summary = "Register with phone + password",
            description = "Create a new customer account using phone number and password")
    public ResponseEntity<?> register(@RequestBody MobileOrderDTO.PhonePasswordRegisterRequest request) {
        try {
            MobileOrderDTO.AuthResponse response = mobileAuthService.registerWithPhone(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    @Operation(summary = "Login with phone + password",
            description = "Authenticate using phone number and password, returns JWT token")
    public ResponseEntity<?> login(@RequestBody MobileOrderDTO.PhonePasswordLoginRequest request) {
        try {
            MobileOrderDTO.AuthResponse response = mobileAuthService.loginWithPhone(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/firebase")
    @Operation(summary = "Authenticate with Firebase token",
            description = "Verify Firebase ID token and return app JWT. Auto-registers if new customer.")
    public ResponseEntity<?> firebaseAuth(@RequestBody MobileOrderDTO.FirebaseAuthRequest request) {
        try {
            // In production, verify the Firebase token server-side using Firebase Admin SDK.
            // For now, we accept the token and extract the UID from it.
            // TODO: Add Firebase Admin SDK verification
            return ResponseEntity.ok(java.util.Map.of(
                    "message",
                    "Firebase auth endpoint ready. Add Firebase Admin SDK dependency and service account key to enable."));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/profile")
    @Operation(summary = "Get customer profile", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> getProfile(@RequestHeader("Authorization") String authHeader) {
        try {
            Long customerId = extractCustomerId(authHeader);
            MobileOrderDTO.CustomerProfile profile = mobileAuthService.getProfile(customerId);
            return ResponseEntity.ok(profile);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/profile")
    @Operation(summary = "Update customer profile", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> updateProfile(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody MobileOrderDTO.UpdateProfileRequest request) {
        try {
            Long customerId = extractCustomerId(authHeader);
            MobileOrderDTO.CustomerProfile profile = mobileAuthService.updateProfile(customerId, request);
            return ResponseEntity.ok(profile);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }

    /**
     * Extract customer ID from the Authorization header's JWT token.
     */
    private Long extractCustomerId(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Missing or invalid Authorization header");
        }
        String token = authHeader.substring(7);
        if (!jwtUtils.validateJwtToken(token)) {
            throw new RuntimeException("Invalid or expired token");
        }
        Long customerId = jwtUtils.getCustomerIdFromToken(token);
        if (customerId == null) {
            throw new RuntimeException("Invalid customer token");
        }
        return customerId;
    }
}
