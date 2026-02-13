package com.example.backend.services.mobile;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.dto.mobile.MobileOrderDTO;
import com.example.backend.model.CustomerEntity;
import com.example.backend.repository.CustomerRepository;
import com.example.backend.security.JwtUtils;
import com.example.backend.services.SystemSettingService;

@Service
public class MobileAuthService {

    private final CustomerRepository customerRepository;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final SystemSettingService systemSettingService;

    public MobileAuthService(CustomerRepository customerRepository, JwtUtils jwtUtils,
            PasswordEncoder passwordEncoder, SystemSettingService systemSettingService) {
        this.customerRepository = customerRepository;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
        this.systemSettingService = systemSettingService;
    }

    /**
     * Register a new customer with phone + password.
     * This is the simple auth flow (no Firebase required).
     */
    @Transactional
    public MobileOrderDTO.AuthResponse registerWithPhone(MobileOrderDTO.PhonePasswordRegisterRequest request) {
        // Check if phone already exists
        Optional<CustomerEntity> existing = customerRepository.findByPhone(request.getPhone());
        if (existing.isPresent()) {
            throw new RuntimeException("Phone number already registered");
        }

        // Create new customer
        CustomerEntity customer = new CustomerEntity();
        customer.setName(request.getName() != null ? request.getName() : "Customer");
        customer.setPhone(request.getPhone());
        customer.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        customer.setStatus(CustomerEntity.Status.ACTIVE);
        customer.setLoyaltyPoints(0);
        customer.setMembershipLevel("BRONZE");
        customer.setFcmToken(request.getFcmToken());

        CustomerEntity saved = customerRepository.save(customer);

        // Generate JWT
        String token = jwtUtils.generateCustomerToken(saved.getCustomerId(), saved.getPhone());

        return MobileOrderDTO.AuthResponse.builder()
                .token(token)
                .customer(toCustomerProfile(saved))
                .build();
    }

    /**
     * Login with phone + password.
     */
    public MobileOrderDTO.AuthResponse loginWithPhone(MobileOrderDTO.PhonePasswordLoginRequest request) {
        CustomerEntity customer = customerRepository.findByPhone(request.getPhone())
                .orElseThrow(() -> new RuntimeException("Phone number not found"));

        if (customer.getPasswordHash() == null ||
                !passwordEncoder.matches(request.getPassword(), customer.getPasswordHash())) {
            throw new RuntimeException("Invalid password");
        }

        // Update FCM token if provided
        if (request.getFcmToken() != null) {
            customer.setFcmToken(request.getFcmToken());
            customerRepository.save(customer);
        }

        String token = jwtUtils.generateCustomerToken(customer.getCustomerId(), customer.getPhone());

        return MobileOrderDTO.AuthResponse.builder()
                .token(token)
                .customer(toCustomerProfile(customer))
                .build();
    }

    /**
     * Authenticate with Firebase ID token.
     * Auto-registers the customer if they don't exist yet.
     */
    @Transactional
    public MobileOrderDTO.AuthResponse authenticateWithFirebase(String firebaseUid, String phone) {
        // Find existing customer by firebase UID or phone
        Optional<CustomerEntity> existing = customerRepository.findByFirebaseUid(firebaseUid);
        CustomerEntity customer;

        if (existing.isPresent()) {
            customer = existing.get();
        } else {
            // Try finding by phone (might have been created via POS)
            Optional<CustomerEntity> byPhone = customerRepository.findByPhone(phone);
            if (byPhone.isPresent()) {
                customer = byPhone.get();
                customer.setFirebaseUid(firebaseUid);
                customer = customerRepository.save(customer);
            } else {
                // Auto-register new customer
                customer = new CustomerEntity();
                customer.setName("Customer");
                customer.setPhone(phone);
                customer.setFirebaseUid(firebaseUid);
                customer.setStatus(CustomerEntity.Status.ACTIVE);
                customer.setLoyaltyPoints(0);
                customer.setMembershipLevel("BRONZE");
                customer = customerRepository.save(customer);
            }
        }

        String token = jwtUtils.generateCustomerToken(customer.getCustomerId(), customer.getPhone());

        return MobileOrderDTO.AuthResponse.builder()
                .token(token)
                .customer(toCustomerProfile(customer))
                .build();
    }

    /**
     * Get customer profile by ID.
     */
    public MobileOrderDTO.CustomerProfile getProfile(Long customerId) {
        CustomerEntity customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        return toCustomerProfile(customer);
    }

    /**
     * Update customer profile.
     */
    @Transactional
    public MobileOrderDTO.CustomerProfile updateProfile(Long customerId, MobileOrderDTO.UpdateProfileRequest request) {
        CustomerEntity customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        if (request.getName() != null) customer.setName(request.getName());
        if (request.getEmail() != null) customer.setEmail(request.getEmail());
        if (request.getAddress() != null) customer.setAddress(request.getAddress());
        if (request.getFcmToken() != null) customer.setFcmToken(request.getFcmToken());

        CustomerEntity saved = customerRepository.save(customer);
        return toCustomerProfile(saved);
    }

    private MobileOrderDTO.CustomerProfile toCustomerProfile(CustomerEntity customer) {
        double redeemRate = 0.01;
        String rateStr = systemSettingService.getValue("LOYALTY_REDEEM_RATE");
        if (rateStr != null) {
            try {
                redeemRate = Double.parseDouble(rateStr);
            } catch (NumberFormatException e) {
                // fall back to default
            }
        }

        return MobileOrderDTO.CustomerProfile.builder()
                .customerId(customer.getCustomerId())
                .name(customer.getName())
                .phone(customer.getPhone())
                .email(customer.getEmail())
                .loyaltyPoints(customer.getLoyaltyPoints())
                .loyaltyRedeemRate(redeemRate)
                .membershipLevel(customer.getMembershipLevel())
                .build();
    }
}
