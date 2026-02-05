package com.example.backend.services;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.dto.AttendanceRequestDTO;
import com.example.backend.dto.AttendanceResponseDTO;
import com.example.backend.mapper.AttendanceMapper;
import com.example.backend.model.AttendanceEntity;
import com.example.backend.model.BranchEntity;
import com.example.backend.model.EmployeeEntity;
import com.example.backend.model.ShiftEntity;
import com.example.backend.repository.AttendanceRepository;
import com.example.backend.repository.EmployeeRepository;
import com.example.backend.repository.ShiftRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;
    private final ShiftRepository shiftRepository; // To check against scheduled shifts
    private final AttendanceMapper attendanceMapper;
    private final QrCodeService qrCodeService;

    // Haversine formula to calculate distance in meters
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371000; // Radius of the earth in meters
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    @Transactional
    public AttendanceResponseDTO mobileClockIn(Long employeeId, String token, Double lat, Double lon) {
        // 1. Validate Token
        Long branchId = qrCodeService.validateToken(token);
        if (branchId == null) {
            throw new RuntimeException("Invalid or expired QR Code");
        }

        // 2. Validate Employee
        EmployeeEntity employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        // 3. Validate Location
        BranchEntity branch = employee.getBranch(); // Assuming employee clocks in at their assigned branch
        
        // If branch has no location set, skip check (or fail, depending on policy)
        if (branch.getLatitude() != null && branch.getLongitude() != null) {
            double distance = calculateDistance(lat, lon, branch.getLatitude(), branch.getLongitude());
            if (distance > branch.getRadiusMeters()) {
                 throw new RuntimeException("You are too far from the branch (" + (int)distance + "m). Max allowed: " + branch.getRadiusMeters() + "m");
            }
        }
        
        // 4. Perform Clock In (Reuse logic)
        return clockIn(employeeId);
    }

    @Transactional
    public AttendanceResponseDTO clockIn(Long employeeId) {
        EmployeeEntity employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        // Check if already clocked in? (Simplified: assuming no open session logic for
        // now, or just create new)
        // In a real app we'd check if there is an attendance with null checkOut.

        AttendanceEntity attendance = new AttendanceEntity();
        attendance.setEmployee(employee);
        attendance.setCheckIn(LocalDateTime.now());

        // Determine Status & Late Minutes based on Assigned Shift today
        // 1. Find shift for today
        LocalDateTime now = LocalDateTime.now();
        List<ShiftEntity> shifts = shiftRepository.findByEmployeeEmployeeIdAndDeletedAtIsNull(employeeId);
        // Filter for today's shift (naive approach)
        ShiftEntity todayShift = shifts.stream()
                .filter(s -> s.getShiftStart().toLocalDate().isEqual(now.toLocalDate()))
                .findFirst()
                .orElse(null);

        if (todayShift != null) {
            if (now.isAfter(todayShift.getShiftStart().plusMinutes(15))) { // 15 min grace period
                attendance.setStatus(AttendanceEntity.AttendanceStatus.LATE);
                long lateMinutes = ChronoUnit.MINUTES.between(todayShift.getShiftStart(), now);
                attendance.setLateMinute((int) lateMinutes);
            } else {
                attendance.setStatus(AttendanceEntity.AttendanceStatus.PRESENT);
                attendance.setLateMinute(0);
            }
        } else {
            // No shift assigned, but clocked in
            attendance.setStatus(AttendanceEntity.AttendanceStatus.PRESENT);
        }

        attendance.setCreatedAt(LocalDateTime.now());
        attendance.setUpdatedAt(LocalDateTime.now());

        AttendanceEntity saved = attendanceRepository.save(attendance);
        return attendanceMapper.toResponseDTO(saved);
    }

    @Transactional
    public AttendanceResponseDTO clockOut(Long employeeId) {
        // Find latest open attendance
        List<AttendanceEntity> records = attendanceRepository.findByEmployeeEmployeeIdAndDeletedAtIsNull(employeeId);
        // Sort descenting by checkIn
        AttendanceEntity lastRecord = records.stream()
                .filter(a -> a.getCheckOut() == null)
                .sorted((a, b) -> b.getCheckIn().compareTo(a.getCheckIn()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No active attendance record found to clock out"));

        lastRecord.setCheckOut(LocalDateTime.now());
        lastRecord.setUpdatedAt(LocalDateTime.now());

        AttendanceEntity saved = attendanceRepository.save(lastRecord);
        return attendanceMapper.toResponseDTO(saved);
    }

    public List<AttendanceResponseDTO> getAllAttendance() {
        return attendanceRepository.findAllByDeletedAtIsNull().stream()
                .map(attendanceMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<AttendanceResponseDTO> getAttendanceByEmployee(Long employeeId) {
        return attendanceRepository.findByEmployeeEmployeeIdAndDeletedAtIsNull(employeeId).stream()
                .map(attendanceMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<AttendanceResponseDTO> getCurrentlyClockedIn() {
        return attendanceRepository.findAllByDeletedAtIsNull().stream()
                .filter(a -> a.getCheckOut() == null)
                .map(attendanceMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    // For admin corrections
    @Transactional
    public AttendanceResponseDTO updateAttendance(Long id, AttendanceRequestDTO request) {
        AttendanceEntity entity = attendanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Attendance not found"));

        attendanceMapper.updateEntityFromDTO(request, entity);
        entity.setUpdatedAt(LocalDateTime.now());
        return attendanceMapper.toResponseDTO(attendanceRepository.save(entity));
    }
}
