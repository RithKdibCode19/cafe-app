package com.example.backend.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.backend.dto.EmployeePerformanceDTO;
import com.example.backend.model.AttendanceEntity;
import com.example.backend.model.DrawerActionEntity;
import com.example.backend.model.EmployeeEntity;
import com.example.backend.model.OrderEntity;
import com.example.backend.repository.AttendanceRepository;
import com.example.backend.repository.DrawerActionRepository;
import com.example.backend.repository.EmployeeRepository;
import com.example.backend.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmployeePerformanceService {

    private final EmployeeRepository employeeRepository;
    private final OrderRepository orderRepository;
    private final AttendanceRepository attendanceRepository;
    private final DrawerActionRepository drawerActionRepository;

    public List<EmployeePerformanceDTO> getPerformanceReport(LocalDateTime start, LocalDateTime end) {
        List<EmployeeEntity> employees = employeeRepository.findAllByDeletedAtIsNull();
        List<OrderEntity> orders = orderRepository.findByCreatedAtBetweenWithItems(start, end);
        List<AttendanceEntity> attendance = attendanceRepository.findByCheckInBetweenAndDeletedAtIsNull(start, end);
        List<DrawerActionEntity> drawerActions = drawerActionRepository.findByActionTimeBetween(start, end);

        // Grouping
        Map<Long, List<OrderEntity>> ordersByEmployee = orders.stream()
                .filter(o -> o.getStatus() == OrderEntity.OrderStatus.PAID || o.getStatus() == OrderEntity.OrderStatus.COMPLETED)
                .collect(Collectors.groupingBy(o -> o.getCashierUser().getEmployee().getEmployeeId()));

        Map<Long, List<AttendanceEntity>> attendanceByEmployee = attendance.stream()
                .collect(Collectors.groupingBy(a -> a.getEmployee().getEmployeeId()));

        Map<Long, List<DrawerActionEntity>> drawerByEmployee = drawerActions.stream()
                .collect(Collectors.groupingBy(d -> d.getUser().getEmployee().getEmployeeId()));

        List<EmployeePerformanceDTO> report = new ArrayList<>();

        for (EmployeeEntity emp : employees) {
            Long empId = emp.getEmployeeId();
            List<OrderEntity> empOrders = ordersByEmployee.getOrDefault(empId, new ArrayList<>());
            List<AttendanceEntity> empAttendance = attendanceByEmployee.getOrDefault(empId, new ArrayList<>());
            List<DrawerActionEntity> empDrawer = drawerByEmployee.getOrDefault(empId, new ArrayList<>());

            double totalSales = empOrders.stream().mapToDouble(OrderEntity::getTotalAmount).sum();
            
            // Upsell Rate calculation: percentage of orders that have at least one item with add-ons
            long ordersWithAddons = empOrders.stream()
                    .filter(o -> o.getItems().stream().anyMatch(i -> i.getAddOnItems() != null && !i.getAddOnItems().isEmpty()))
                    .count();

            int totalLateMinutes = empAttendance.stream()
                    .mapToInt(a -> a.getLateMinute() != null ? a.getLateMinute() : 0)
                    .sum();

            long totalShifts = empAttendance.size();
            long onTimeShifts = empAttendance.stream()
                    .filter(a -> a.getStatus() == AttendanceEntity.AttendanceStatus.PRESENT)
                    .count();

            report.add(EmployeePerformanceDTO.builder()
                    .employeeId(empId)
                    .fullName(emp.getFullName())
                    .totalOrders((long) empOrders.size())
                    .totalSales(totalSales)
                    .avgTransactionValue(empOrders.isEmpty() ? 0.0 : totalSales / empOrders.size())
                    .upsellRate(empOrders.isEmpty() ? 0.0 : (double) ordersWithAddons / empOrders.size() * 100)
                    .totalLateMinutes(totalLateMinutes)
                    .onTimeShifts(onTimeShifts)
                    .totalShifts(totalShifts)
                    .manualDrawerOpens((long) empDrawer.size())
                    .build());
        }

        return report;
    }
}
