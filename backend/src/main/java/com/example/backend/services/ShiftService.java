package com.example.backend.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.dto.ShiftRequestDTO;
import com.example.backend.dto.ShiftResponseDTO;
import com.example.backend.mapper.ShiftMapper;
import com.example.backend.model.BranchEntity;
import com.example.backend.model.EmployeeEntity;
import com.example.backend.model.ShiftEntity;
import com.example.backend.repository.BranchRepository;
import com.example.backend.repository.EmployeeRepository;
import com.example.backend.repository.ShiftRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ShiftService {

    private final ShiftRepository shiftRepository;
    private final EmployeeRepository employeeRepository;
    private final BranchRepository branchRepository;
    private final ShiftMapper shiftMapper;

    @Transactional
    public ShiftResponseDTO createShift(ShiftRequestDTO request) {
        EmployeeEntity employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        BranchEntity branch = branchRepository.findById(request.getBranchId())
                .orElseThrow(() -> new RuntimeException("Branch not found"));

        ShiftEntity shift = shiftMapper.toEntity(request);
        shift.setEmployee(employee);
        shift.setBranch(branch);
        shift.setCreatedAt(LocalDateTime.now());
        shift.setUpdatedAt(LocalDateTime.now());

        ShiftEntity savedShift = shiftRepository.save(shift);
        return shiftMapper.toResponseDTO(savedShift);
    }

    public List<ShiftResponseDTO> getAllShifts() {
        return shiftRepository.findAllByDeletedAtIsNull().stream()
                .map(shiftMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<ShiftResponseDTO> getShiftsByEmployee(Long employeeId) {
        return shiftRepository.findByEmployeeEmployeeIdAndDeletedAtIsNull(employeeId).stream()
                .map(shiftMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<ShiftResponseDTO> getShiftsByBranch(Long branchId) {
        return shiftRepository.findByBranchBranchIdAndDeletedAtIsNull(branchId).stream()
                .map(shiftMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ShiftResponseDTO updateShift(Long id, ShiftRequestDTO request) {
        ShiftEntity shift = shiftRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shift not found"));

        if (shift.getDeletedAt() != null) {
            throw new RuntimeException("Cannot update deleted shift");
        }

        if (!shift.getEmployee().getEmployeeId().equals(request.getEmployeeId())) {
            EmployeeEntity employee = employeeRepository.findById(request.getEmployeeId())
                    .orElseThrow(() -> new RuntimeException("Employee not found"));
            shift.setEmployee(employee);
        }

        if (!shift.getBranch().getBranchId().equals(request.getBranchId())) {
            BranchEntity branch = branchRepository.findById(request.getBranchId())
                    .orElseThrow(() -> new RuntimeException("Branch not found"));
            shift.setBranch(branch);
        }

        shiftMapper.updateEntityFromDTO(request, shift);
        shift.setUpdatedAt(LocalDateTime.now());

        ShiftEntity updatedShift = shiftRepository.save(shift);
        return shiftMapper.toResponseDTO(updatedShift);
    }

    @Transactional
    public void deleteShift(Long id) {
        ShiftEntity shift = shiftRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shift not found"));
        shift.setDeletedAt(LocalDateTime.now());
        shift.setUpdatedAt(LocalDateTime.now());
        shiftRepository.save(shift);
    }
}
