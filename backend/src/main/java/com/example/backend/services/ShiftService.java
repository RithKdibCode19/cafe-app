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
    private final com.example.backend.repository.UserRepository userRepository;
    private final com.example.backend.repository.OrderRepository orderRepository;
    private final com.example.backend.repository.PaymentRepository paymentRepository;
    private final ShiftMapper shiftMapper;
    private final TelegramService telegramService;

    @Transactional
    public ShiftResponseDTO createShift(ShiftRequestDTO request) {
        // ... (existing logic)
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
        // ... (existing logic)
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

    private EmployeeEntity getEmployeeByUserId(Long userId) {
        return userRepository.findById(userId)
                .map(com.example.backend.model.UserEntity::getEmployee)
                .orElseThrow(() -> new RuntimeException("User not found or not linked to employee"));
    }

    public ShiftResponseDTO getCurrentShift(Long userId) {
        EmployeeEntity employee = getEmployeeByUserId(userId);
        return shiftRepository.findFirstByEmployee_EmployeeIdAndEndTimeIsNullAndDeletedAtIsNull(employee.getEmployeeId())
                .map(shiftMapper::toResponseDTO)
                .orElse(null);
    }

    @Transactional
    public ShiftResponseDTO openShift(Long userId, Double startingCash) {
        if (getCurrentShift(userId) != null) {
            throw new RuntimeException("User already has an open shift");
        }

        EmployeeEntity employee = getEmployeeByUserId(userId);
        BranchEntity branch = employee.getBranch(); 
        if (branch == null) {
             throw new RuntimeException("Employee is not assigned to a branch");
        }

        ShiftEntity shift = new ShiftEntity();
        shift.setEmployee(employee);
        shift.setBranch(branch);
        shift.setStartTime(LocalDateTime.now());
        shift.setStartCash(startingCash);
        shift.setCreatedAt(LocalDateTime.now());
        shift.setUpdatedAt(LocalDateTime.now());

        return shiftMapper.toResponseDTO(shiftRepository.save(shift));
    }

    public com.example.backend.dto.report.ShiftSummaryDTO getShiftSummary(Long userId) {
        EmployeeEntity employee = getEmployeeByUserId(userId);
        ShiftEntity shift = shiftRepository.findFirstByEmployee_EmployeeIdAndEndTimeIsNullAndDeletedAtIsNull(employee.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("No active shift found"));

        com.example.backend.dto.report.ShiftSummaryDTO summary = new com.example.backend.dto.report.ShiftSummaryDTO();
        summary.setEmployeeId(employee.getEmployeeId());
        summary.setEmployeeName(employee.getFullName());
        summary.setCheckInTime(shift.getStartTime().toString());
        summary.setStatus("OPEN");
        summary.setStartingCash(shift.getStartCash());

        List<com.example.backend.model.OrderEntity> orders = orderRepository.findByCashierUserUserIdAndCreatedAtBetweenAndDeletedAtIsNull(
                userId, shift.getStartTime(), LocalDateTime.now());

        double totalSales = 0.0;
        java.util.Map<String, Double> salesByMethod = new java.util.HashMap<>();
        salesByMethod.put("CASH", 0.0);
        salesByMethod.put("CARD", 0.0);
        salesByMethod.put("QR", 0.0);

        for (com.example.backend.model.OrderEntity order : orders) {
            if (order.getStatus() == com.example.backend.model.OrderEntity.OrderStatus.PAID) {
                totalSales += order.getTotalAmount();

                List<com.example.backend.model.PaymentEntity> payments = paymentRepository
                        .findByOrderOrderIdAndDeletedAtIsNull(order.getOrderId());
                for (com.example.backend.model.PaymentEntity p : payments) {
                     // Handle null method safely
                    String method = (p.getMethod() != null) ? p.getMethod().name() : "Other";
                    salesByMethod.put(method, salesByMethod.getOrDefault(method, 0.0) + p.getPaidAmount());
                }
            }
        }

        summary.setTotalSales(totalSales);
        summary.setSalesByMethod(salesByMethod);
        summary.setTotalOrders(orders.size());
        
         // Expected Cash = Starting Cash + Cash Sales
        double cashSales = salesByMethod.getOrDefault("CASH", 0.0);
        summary.setExpectedCash(shift.getStartCash() + cashSales);

        return summary;
    }

    @Transactional
    public ShiftResponseDTO closeShift(Long userId, Double endingCash) {
        EmployeeEntity employee = getEmployeeByUserId(userId);
        ShiftEntity shift = shiftRepository.findFirstByEmployee_EmployeeIdAndEndTimeIsNullAndDeletedAtIsNull(employee.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("No active shift found"));

        shift.setEndTime(LocalDateTime.now());
        shift.setEndCash(endingCash);
        shift.setUpdatedAt(LocalDateTime.now());

        ShiftEntity savedShift = shiftRepository.save(shift);

        // Calculate expected cash and check for discrepancy
        try {
            double expectedCash = calculateExpectedCash(shift);
            double discrepancy = endingCash - expectedCash;
            
            // Alert if discrepancy is more than $1 (tolerance for rounding)
            if (Math.abs(discrepancy) > 1.0) {
                telegramService.sendShiftDiscrepancyAlert(
                    employee.getFullName(),
                    expectedCash,
                    endingCash,
                    discrepancy
                );
            }
        } catch (Exception e) {
            // Don't fail shift close if notification fails
        }

        return shiftMapper.toResponseDTO(savedShift);
    }

    private double calculateExpectedCash(ShiftEntity shift) {
        // Use the existing repository method to calculate cash payments during shift
        Double cashSales = paymentRepository.calculateTotalPaymentsByMethodForPeriod(
            com.example.backend.model.PaymentEntity.PaymentMethod.CASH,
            shift.getStartTime(),
            LocalDateTime.now()
        );
        
        return (shift.getStartCash() != null ? shift.getStartCash() : 0.0) + 
               (cashSales != null ? cashSales : 0.0);
    }
}
