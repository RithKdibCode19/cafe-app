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


@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final BranchRepository branchRepository;
    private final com.example.backend.repository.UserRepository userRepository;
    private final com.example.backend.repository.RoleRepository roleRepository;
    private final EmployeeMapper employeeMapper;

    public EmployeeService(EmployeeRepository employeeRepository, BranchRepository branchRepository, 
                           com.example.backend.repository.UserRepository userRepository,
                           com.example.backend.repository.RoleRepository roleRepository,
                           EmployeeMapper employeeMapper) {
        this.employeeRepository = employeeRepository;
        this.branchRepository = branchRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.employeeMapper = employeeMapper;
    }

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

        // 4. Update associated user role if roleId is provided
        if (request.getRoleId() != null) {
            userRepository.findByEmployeeEmployeeIdAndDeletedAtIsNull(savedEmployee.getEmployeeId())
                    .ifPresent(user -> {
                        roleRepository.findById(request.getRoleId())
                                .ifPresent(user::setRole);
                        userRepository.save(user);
                    });
        }

        // 5. Map Entity → Response DTO
        EmployeeResponseDTO response = employeeMapper.toResponseDTO(savedEmployee);
        enrichWithRole(response, savedEmployee.getEmployeeId());
        return response;
    }

    private void enrichWithRole(EmployeeResponseDTO dto, Long employeeId) {
        userRepository.findByEmployeeEmployeeIdAndDeletedAtIsNull(employeeId)
                .ifPresent(user -> {
                    if (user.getRole() != null) {
                        dto.setRoleId(user.getRole().getRoleId());
                        dto.setRoleName(user.getRole().getRoleName());
                    }
                });
    }

    public List<EmployeeResponseDTO> getAllEmployees() {
        List<EmployeeEntity> employees = employeeRepository.findAllActiveEmployees();
        return employees.stream()
                .map(emp -> {
                    EmployeeResponseDTO dto = employeeMapper.toResponseDTO(emp);
                    enrichWithRole(dto, emp.getEmployeeId());
                    return dto;
                })
                .toList();
    }

    public EmployeeResponseDTO getEmployeeById(Long id) {
        EmployeeEntity employee = employeeRepository.findActiveEmployeeById(id);
        if (employee == null) {
            throw new RuntimeException("Employee not found");
        }
        EmployeeResponseDTO dto = employeeMapper.toResponseDTO(employee);
        enrichWithRole(dto, id);
        return dto;
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

        // 4. Update user role if roleId is provided
        if (request.getRoleId() != null) {
            userRepository.findByEmployeeEmployeeIdAndDeletedAtIsNull(id)
                    .ifPresent(user -> {
                        roleRepository.findById(request.getRoleId())
                                .ifPresent(user::setRole);
                        userRepository.save(user);
                    });
        }

        // 5. Save and return
        EmployeeEntity updatedEmployee = employeeRepository.save(employee);
        EmployeeResponseDTO response = employeeMapper.toResponseDTO(updatedEmployee);
        enrichWithRole(response, id);
        return response;
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
