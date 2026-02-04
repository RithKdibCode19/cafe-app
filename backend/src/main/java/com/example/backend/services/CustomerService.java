package com.example.backend.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.backend.dto.CustomerRequestDTO;
import com.example.backend.dto.CustomerResponseDTO;
import com.example.backend.mapper.CustomerMapper;
import com.example.backend.model.CustomerEntity;
import com.example.backend.repository.CustomerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    /**
     * Create a new customer
     */
    public CustomerResponseDTO createCustomer(CustomerRequestDTO request) {
        CustomerEntity customerEntity = customerMapper.toEntity(request);
        CustomerEntity savedCustomer = customerRepository.save(customerEntity);
        return customerMapper.toResponseDTO(savedCustomer);
    }

    /**
     * Get all customers (active only)
     */
    public List<CustomerResponseDTO> getAllCustomers() {
        List<CustomerEntity> customers = customerRepository.findAll();
        return customers.stream()
                .filter(c -> c.getDeletedAt() == null)
                .map(customerMapper::toResponseDTO)
                .toList();
    }

    /**
     * Get customer by ID
     */
    public CustomerResponseDTO getCustomerById(Long id) {
        CustomerEntity customerEntity = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + id));
        return customerMapper.toResponseDTO(customerEntity);
    }

    /**
     * Update customer
     */
    public CustomerResponseDTO updateCustomer(Long id, CustomerRequestDTO request) {
        CustomerEntity customerEntity = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + id));
        customerMapper.updateEntityFromDTO(request, customerEntity);
        CustomerEntity updatedCustomer = customerRepository.save(customerEntity);
        return customerMapper.toResponseDTO(updatedCustomer);
    }

    /**
     * Soft delete customer
     */
    public void deleteCustomer(Long id) {
        CustomerEntity customerEntity = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + id));
        customerEntity.setDeletedAt(LocalDateTime.now());
        customerRepository.save(customerEntity);
    }
    /**
     * Search customers
     */
    public List<CustomerResponseDTO> searchCustomers(String query) {
        List<CustomerEntity> customers = customerRepository.searchCustomers(query);
         return customers.stream()
                .map(customerMapper::toResponseDTO)
                .toList();
    }
}
