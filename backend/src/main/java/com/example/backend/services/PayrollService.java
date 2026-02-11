package com.example.backend.services;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.backend.dto.staff.PayrollResponseDTO;
import com.example.backend.model.AttendanceEntity;
import com.example.backend.model.EmployeeEntity;
import com.example.backend.repository.AttendanceRepository;
import com.example.backend.repository.EmployeeRepository;


@Service
public class PayrollService {

    private final EmployeeRepository employeeRepository;
    private final AttendanceRepository attendanceRepository;

    public PayrollService(EmployeeRepository employeeRepository, AttendanceRepository attendanceRepository) {
        this.employeeRepository = employeeRepository;
        this.attendanceRepository = attendanceRepository;
    }

    public List<PayrollResponseDTO> generatePayrollReport(LocalDate startDate, LocalDate endDate) {
        List<EmployeeEntity> employees = employeeRepository.findAllByDeletedAtIsNull();
        List<PayrollResponseDTO> payrollList = new ArrayList<>();

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        for (EmployeeEntity employee : employees) {
            List<AttendanceEntity> attendanceRecords = attendanceRepository
                    .findByEmployeeEmployeeIdAndCheckInBetweenAndDeletedAtIsNull(
                            employee.getEmployeeId(), startDateTime, endDateTime);

            double totalHours = 0;
            int lateOccurrences = 0;
            int totalLateMinutes = 0;
            int daysWorked = (int) attendanceRecords.stream()
                    .map(a -> a.getCheckIn().toLocalDate())
                    .distinct()
                    .count();

            for (AttendanceEntity record : attendanceRecords) {
                if (record.getCheckOut() != null) {
                    Duration duration = Duration.between(record.getCheckIn(), record.getCheckOut());
                    totalHours += duration.toMinutes() / 60.0;
                }

                if (record.getStatus() == AttendanceEntity.AttendanceStatus.LATE) {
                    lateOccurrences++;
                    totalLateMinutes += (record.getLateMinute() != null ? record.getLateMinute() : 0);
                }
            }

            double hourlyRate = employee.getHourlyRate() != null ? employee.getHourlyRate() : 0.0;
            double baseSalary = employee.getBaseSalary() != null ? employee.getBaseSalary() : 0.0;
            double hourlyEarnings = totalHours * hourlyRate;
            double totalEarnings = baseSalary + hourlyEarnings; // Assuming base salary is per period

            payrollList.add(new PayrollResponseDTO(
                    employee.getEmployeeId(),
                    employee.getFullName(),
                    employee.getPosition(),
                    baseSalary,
                    hourlyRate,
                    totalHours,
                    hourlyEarnings,
                    totalEarnings,
                    daysWorked,
                    lateOccurrences,
                    totalLateMinutes
            ));
        }

        return payrollList;
    }
}
