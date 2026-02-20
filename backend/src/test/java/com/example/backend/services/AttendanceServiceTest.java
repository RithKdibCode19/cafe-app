package com.example.backend.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.List;
import java.util.Collections;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.backend.dto.AttendanceResponseDTO;
import com.example.backend.mapper.AttendanceMapper;
import com.example.backend.model.AttendanceEntity;
import com.example.backend.model.EmployeeEntity;
import com.example.backend.repository.AttendanceRepository;
import com.example.backend.repository.EmployeeRepository;
import com.example.backend.repository.ShiftRepository;

@ExtendWith(MockitoExtension.class)
public class AttendanceServiceTest {

    @Mock private AttendanceRepository attendanceRepository;
    @Mock private EmployeeRepository employeeRepository;
    @Mock private ShiftRepository shiftRepository;
    @Mock private AttendanceMapper attendanceMapper;

    @InjectMocks private AttendanceService service;

    @Test
    void clockIn_Success() {
        Long empId = 1L;
        EmployeeEntity employee = new EmployeeEntity();
        employee.setEmployeeId(empId);

        when(employeeRepository.findById(empId)).thenReturn(Optional.of(employee));
        when(shiftRepository.findByEmployeeEmployeeIdAndDeletedAtIsNull(empId)).thenReturn(Collections.emptyList());
        
        AttendanceEntity savedEntity = new AttendanceEntity();
        savedEntity.setAttendanceId(100L);
        when(attendanceRepository.save(any(AttendanceEntity.class))).thenReturn(savedEntity);
        
        AttendanceResponseDTO response = new AttendanceResponseDTO();
        response.setAttendanceId(100L);
        when(attendanceMapper.toResponseDTO(savedEntity)).thenReturn(response);

        AttendanceResponseDTO result = service.clockIn(empId);
        
        assertNotNull(result);
        verify(attendanceRepository).save(any(AttendanceEntity.class));
    }
}
