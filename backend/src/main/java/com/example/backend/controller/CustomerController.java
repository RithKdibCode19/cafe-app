package com.example.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.dto.CustomerRequestDTO;
import com.example.backend.dto.CustomerResponseDTO;
import com.example.backend.dto.common.ApiResponse;
import com.example.backend.dto.customer.CustomerHistoryDTO;
import com.example.backend.services.CustomerHistoryService;
import com.example.backend.services.CustomerService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;
    private final CustomerHistoryService customerHistoryService;

    @PostMapping("/add")
    public ResponseEntity<CustomerResponseDTO> createCustomer(@jakarta.validation.Valid @RequestBody CustomerRequestDTO request) {
        CustomerResponseDTO response = customerService.createCustomer(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<CustomerResponseDTO>> getAllCustomers() {
        List<CustomerResponseDTO> response = customerService.getAllCustomers();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> getCustomerById(@PathVariable Long id) {
        CustomerResponseDTO response = customerService.getCustomerById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> updateCustomer(@PathVariable Long id,
            @RequestBody CustomerRequestDTO request) {
        CustomerResponseDTO response = customerService.updateCustomer(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<CustomerResponseDTO>> searchCustomers(@RequestParam String query) {
        List<CustomerResponseDTO> response = customerService.searchCustomers(query);
        return ResponseEntity.ok(response);
    }

    // ========== Customer History Endpoints ==========

    @GetMapping("/{id}/history")
    public ResponseEntity<ApiResponse<CustomerHistoryDTO>> getCustomerHistory(@PathVariable Long id) {
        try {
            CustomerHistoryDTO history = customerHistoryService.getCustomerHistory(id);
            return ResponseEntity.ok(ApiResponse.success(history, "Customer history retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/history/by-phone")
    public ResponseEntity<ApiResponse<CustomerHistoryDTO>> getCustomerHistoryByPhone(@RequestParam String phone) {
        try {
            CustomerHistoryDTO history = customerHistoryService.getCustomerHistoryByPhone(phone);
            return ResponseEntity.ok(ApiResponse.success(history, "Customer history retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
