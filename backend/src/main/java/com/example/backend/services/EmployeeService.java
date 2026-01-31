package com.example.backend.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.dto.EmployeeRequestDTO;
import com.example.backend.dto.EmployeeResponseDTO;
import com.example.backend.mapper.EmployeeMapper;
import com.example.backend.model.BranchEntity;
import com.example.backend.model.EmployeeEntity;
import com.example.backend.model.EmployeeEntity.Status;
import com.example.backend.repository.BranchRepository;
import com.example.backend.repository.EmployeeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final BranchRepository branchRepository;
    private final EmployeeMapper employeeMapper;

    @Transactional
    public EmployeeResponseDTO createEmployee(EmployeeRequestDTO request) {
        // 1. Validate and fetch branch
        BranchEntity branch = branchRepository.findById(request.getBranchId())
                .orElseThrow(() -> new RuntimeException("Branch not found"));

        // 2. Map Request DTO → Entity
        EmployeeEntity employee = employeeMapper.toEntity(request);
        employee.setBranch(branch);
        employee.setStatus(Status.valueOf(request.getStatus() != null ? request.getStatus() : "ACTIVE"));

        // 3. Save entity
        EmployeeEntity savedEmployee = employeeRepository.save(employee);

        // 4. Map Entity → Response DTO
        return employeeMapper.toResponseDTO(savedEmployee);
    }

    public List<EmployeeResponseDTO> getAllEmployees() {
        List<EmployeeEntity> employees = employeeRepository.findAllActiveEmployees();
        return employees.stream()
                .map(employeeMapper::toResponseDTO)
                .toList();
    }

    public EmployeeResponseDTO getEmployeeById(Long id) {
        EmployeeEntity employee = employeeRepository.findActiveEmployeeById(id);
        if (employee == null) {
            throw new RuntimeException("Employee not found");
        }
        return employeeMapper.toResponseDTO(employee);
    }

    @Transactional
    public EmployeeResponseDTO updateEmployee(Long id, EmployeeRequestDTO request) {
        // 1. Fetch existing employee
        EmployeeEntity employee = employeeRepository.findActiveEmployeeById(id);
        if (employee == null) {
            throw new RuntimeException("Employee not found");
        }

        // 2. Validate and update branch if changed
        if (request.getBranchId() != null && employee.getBranch() != null
                && !request.getBranchId().equals(employee.getBranch().getBranchId())) {
            BranchEntity branch = branchRepository.findById(request.getBranchId())
                    .orElseThrow(() -> new RuntimeException("Branch not found"));
            employee.setBranch(branch);
        } else if (request.getBranchId() != null && employee.getBranch() == null) {
            BranchEntity branch = branchRepository.findById(request.getBranchId())
                    .orElseThrow(() -> new RuntimeException("Branch not found"));
            employee.setBranch(branch);
        }

        // 3. Update from DTO
        employeeMapper.updateEntityFromDTO(request, employee);
        if (request.getStatus() != null) {
            employee.setStatus(Status.valueOf(request.getStatus()));
        }

        // 4. Save and return
        EmployeeEntity updatedEmployee = employeeRepository.save(employee);
        return employeeMapper.toResponseDTO(updatedEmployee);
    }

    @Transactional
    public void deleteEmployee(Long id) {
        EmployeeEntity employee = employeeRepository.findActiveEmployeeById(id);
        if (employee == null) {
            throw new RuntimeException("Employee not found");
        }

        employee.setDeletedAt(LocalDateTime.now());
        employee.setDeletedBy(null);
        employee.setStatus(Status.INACTIVE);
        employeeRepository.save(employee);
    }
}
