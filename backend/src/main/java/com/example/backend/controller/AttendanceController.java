package com.example.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.dto.AttendanceRequestDTO;
import com.example.backend.dto.AttendanceResponseDTO;
import com.example.backend.services.AttendanceService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;
    private final com.example.backend.services.QrCodeService qrCodeService;

    @PostMapping("/mobile-check-in/{employeeId}")
    public ResponseEntity<AttendanceResponseDTO> mobileClockIn(
            @PathVariable Long employeeId,
            @RequestBody com.example.backend.dto.MobileCheckInRequestDTO request) {
        return ResponseEntity.ok(attendanceService.mobileClockIn(employeeId, request.getToken(), request.getLatitude(), request.getLongitude()));
    }

    @GetMapping("/qr-token/{branchId}")
    public ResponseEntity<String> getQrToken(@PathVariable Long branchId) {
        return ResponseEntity.ok(qrCodeService.generateQrToken(branchId));
    }

    @PostMapping("/clock-in/{employeeId}")
    public ResponseEntity<AttendanceResponseDTO> clockIn(@PathVariable Long employeeId) {
        return ResponseEntity.ok(attendanceService.clockIn(employeeId));
    }

    @PostMapping("/clock-out/{employeeId}")
    public ResponseEntity<AttendanceResponseDTO> clockOut(@PathVariable Long employeeId) {
        return ResponseEntity.ok(attendanceService.clockOut(employeeId));
    }

    @GetMapping
    public ResponseEntity<List<AttendanceResponseDTO>> getAllAttendance() {
        return ResponseEntity.ok(attendanceService.getAllAttendance());
    }

    @GetMapping("/active")
    public ResponseEntity<List<AttendanceResponseDTO>> getActiveAttendance() {
        return ResponseEntity.ok(attendanceService.getCurrentlyClockedIn());
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<AttendanceResponseDTO>> getAttendanceByEmployee(@PathVariable Long employeeId) {
        return ResponseEntity.ok(attendanceService.getAttendanceByEmployee(employeeId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AttendanceResponseDTO> updateAttendance(@PathVariable Long id,
            @Valid @RequestBody AttendanceRequestDTO request) {
        return ResponseEntity.ok(attendanceService.updateAttendance(id, request));
    }
}
