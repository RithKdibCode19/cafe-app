package com.example.backend.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.backend.dto.ShiftRequestDTO;
import com.example.backend.dto.ShiftResponseDTO;
import com.example.backend.mapper.ShiftMapper;
import com.example.backend.model.BranchEntity;
import com.example.backend.model.EmployeeEntity;
import com.example.backend.model.ShiftEntity;
import com.example.backend.repository.BranchRepository;
import com.example.backend.repository.EmployeeRepository;
import com.example.backend.repository.ShiftRepository;

@ExtendWith(MockitoExtension.class)
public class ShiftServiceTest {

    @Mock
    private ShiftRepository shiftRepository;
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private BranchRepository branchRepository;
    @Mock
    private ShiftMapper shiftMapper;

    @InjectMocks
    private ShiftService service;

    @Test
    void createShift_Success() {
        ShiftRequestDTO req = new ShiftRequestDTO();
        req.setEmployeeId(1L);
        req.setBranchId(2L);

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(new EmployeeEntity()));
        when(branchRepository.findById(2L)).thenReturn(Optional.of(new BranchEntity()));

        ShiftEntity shift = new ShiftEntity();
        when(shiftMapper.toEntity(req)).thenReturn(shift);
        when(shiftRepository.save(any(ShiftEntity.class))).thenReturn(shift);
        when(shiftMapper.toResponseDTO(shift)).thenReturn(new ShiftResponseDTO());

        assertDoesNotThrow(() -> service.createShift(req));
    }
}
