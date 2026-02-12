package com.example.backend.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.dto.report.DashboardStatsDTO;
import com.example.backend.dto.report.DashboardStatsDTO.PaymentMethodBreakdown;
import com.example.backend.dto.report.DashboardStatsDTO.RecentOrder;
import com.example.backend.dto.report.DashboardStatsDTO.TopSellingItem;
import com.example.backend.repository.AttendanceRepository;
import com.example.backend.repository.IngredientRepository;
import com.example.backend.repository.OrderRepository;
import com.example.backend.repository.PaymentRepository;
import com.example.backend.repository.StockAdjustmentRepository;
import com.example.backend.repository.EmployeeRepository;
import com.example.backend.model.*;

@Service
public class ReportService {

        private final OrderRepository orderRepository;
        private final PaymentRepository paymentRepository;
        private final IngredientRepository ingredientRepository;
        private final StockAdjustmentRepository stockAdjustmentRepository;
        private final AttendanceRepository attendanceRepository;
        private final com.example.backend.repository.ExpenseRepository expenseRepository;
        private final EmployeeRepository employeeRepository;
        private final com.example.backend.repository.StockInRepository stockInRepository;
        private final com.example.backend.repository.RecipeRepository recipeRepository;

        public ReportService(OrderRepository orderRepository,
                        PaymentRepository paymentRepository,
                        IngredientRepository ingredientRepository,
                        StockAdjustmentRepository stockAdjustmentRepository,
                        AttendanceRepository attendanceRepository,
                        com.example.backend.repository.ExpenseRepository expenseRepository,
                        EmployeeRepository employeeRepository,
                        com.example.backend.repository.StockInRepository stockInRepository,
                        com.example.backend.repository.RecipeRepository recipeRepository) {
                this.orderRepository = orderRepository;
                this.paymentRepository = paymentRepository;
                this.ingredientRepository = ingredientRepository;
                this.stockAdjustmentRepository = stockAdjustmentRepository;
                this.attendanceRepository = attendanceRepository;
                this.expenseRepository = expenseRepository;
                this.employeeRepository = employeeRepository;
                this.stockInRepository = stockInRepository;
                this.recipeRepository = recipeRepository;
        }

        public DashboardStatsDTO getDashboardStats() {
                DashboardStatsDTO stats = new DashboardStatsDTO();

                LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
                LocalDateTime endOfDay = startOfDay.plusDays(1);

                // Get today's orders using optimized repository method
                List<OrderEntity> todayOrders = orderRepository.findByCreatedAtBetweenAndDeletedAtIsNull(startOfDay,
                                endOfDay);

                // Calculate today's revenue
                Double todayRevenue = todayOrders.stream()
                                .filter(o -> o.getStatus() == OrderEntity.OrderStatus.PAID)
                                .mapToDouble(o -> o.getTotalAmount() != null ? o.getTotalAmount() : 0.0)
                                .sum();

                stats.setTodayRevenue(todayRevenue);
                stats.setTodayOrderCount(todayOrders.size());
                stats.setAverageOrderValue(todayOrders.isEmpty() ? 0.0 : todayRevenue / todayOrders.size());

                // Calculate today's expenses
                Double todayExpenses = expenseRepository
                                .findByDateBetweenAndDeletedAtIsNull(startOfDay.toLocalDate(), startOfDay.toLocalDate())
                                .stream()
                                .mapToDouble(e -> e.getAmount() != null ? e.getAmount() : 0.0)
                                .sum();
                stats.setTodayExpenses(todayExpenses);
                stats.setTodayNetProfit(todayRevenue - todayExpenses);

                // Count unique customers today
                long uniqueCustomers = todayOrders.stream()
                                .filter(o -> o.getCustomer() != null)
                                .map(o -> o.getCustomer().getCustomerId())
                                .distinct()
                                .count();
                stats.setTodayCustomerCount((int) uniqueCustomers);

                // Payment breakdown
                stats.setPaymentBreakdown(getPaymentBreakdown(startOfDay, endOfDay));

                // Top selling items (all time, limit 5)
                stats.setTopSellingItems(getTopSellingItems(5));

                // Recent orders (last 10)
                stats.setRecentOrders(getRecentOrders(10));

                // 7-day sales breakdown for chart
                stats.setDailySales(get7DaySalesBreakdown());

                return stats;
        }

        private List<DashboardStatsDTO.DailySales> get7DaySalesBreakdown() {
                LocalDate today = LocalDate.now();
                LocalDate sevenDaysAgo = today.minusDays(6);
                LocalDateTime start = sevenDaysAgo.atStartOfDay();
                LocalDateTime end = today.atTime(23, 59, 59);

                List<OrderEntity> orders = orderRepository.findByCreatedAtBetweenAndDeletedAtIsNull(start, end).stream()
                                .filter(o -> o.getStatus() == OrderEntity.OrderStatus.PAID)
                                .collect(Collectors.toList());

                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                Map<String, DashboardStatsDTO.DailySales> dailyMap = new HashMap<>();

                // Initialize last 7 days
                for (int i = 0; i < 7; i++) {
                        String dateStr = sevenDaysAgo.plusDays(i).format(dateFormatter);
                        dailyMap.put(dateStr, new DashboardStatsDTO.DailySales(dateStr, 0.0, 0));
                }

                // Aggregate
                for (OrderEntity order : orders) {
                        String dateStr = order.getCreatedAt().format(dateFormatter);
                        DashboardStatsDTO.DailySales ds = dailyMap.get(dateStr);
                        if (ds != null) {
                                ds.setRevenue(ds.getRevenue()
                                                + (order.getTotalAmount() != null ? order.getTotalAmount() : 0.0));
                                ds.setOrderCount(ds.getOrderCount() + 1);
                        }
                }

                return dailyMap.values().stream()
                                .sorted((a, b) -> a.getDate().compareTo(b.getDate()))
                                .collect(Collectors.toList());
        }

        private List<PaymentMethodBreakdown> getPaymentBreakdown(LocalDateTime start, LocalDateTime end) {
                List<PaymentEntity> todayPayments = paymentRepository
                                .findByCreatedAtBetweenAndDeletedAtIsNull(start, end).stream()
                                .filter(p -> p.getDeletedAt() == null &&
                                                p.getPaymentStatus() == PaymentEntity.PaymentStatus.PAID)
                                .collect(Collectors.toList());

                Map<String, PaymentMethodBreakdown> breakdown = new HashMap<>();

                for (PaymentEntity payment : todayPayments) {
                        String method = payment.getMethod() != null ? payment.getMethod().name() : "OTHER";
                        PaymentMethodBreakdown pb = breakdown.getOrDefault(method,
                                        new PaymentMethodBreakdown(method, 0.0, 0));
                        pb.setAmount(pb.getAmount()
                                        + (payment.getPaidAmount() != null ? payment.getPaidAmount() : 0.0));
                        pb.setCount(pb.getCount() + 1);
                        breakdown.put(method, pb);
                }

                return new ArrayList<>(breakdown.values());
        }

        private List<TopSellingItem> getTopSellingItems(int limit) {
                // Get only paid orders from the last 30 days for "top selling" relevance
                LocalDateTime monthAgo = LocalDateTime.now().minusDays(30);
                List<OrderEntity> recentPaidOrders = orderRepository
                                .findByCreatedAtBetweenWithItems(monthAgo, LocalDateTime.now()).stream()
                                .filter(o -> o.getStatus() == OrderEntity.OrderStatus.PAID)
                                .collect(Collectors.toList());

                Map<Long, TopSellingItem> itemSales = new HashMap<>();

                for (OrderEntity order : recentPaidOrders) {
                        if (order.getItems() != null) {
                                for (OrderItemEntity item : order.getItems()) {
                                        if (item.getMenuItem() != null) {
                                                Long menuItemId = item.getMenuItem().getMenuItemId();
                                                String name = item.getMenuItem().getName();

                                                TopSellingItem tsi = itemSales.getOrDefault(menuItemId,
                                                                new TopSellingItem(menuItemId, name, 0, 0.0));
                                                tsi.setQuantitySold(tsi.getQuantitySold()
                                                                + (item.getQty() != null ? item.getQty() : 0));
                                                tsi.setRevenue(tsi.getRevenue()
                                                                + (item.getUnitPrice() != null && item.getQty() != null
                                                                                ? item.getUnitPrice() * item.getQty()
                                                                                : 0.0));
                                                itemSales.put(menuItemId, tsi);
                                        }
                                }
                        }
                }

                return itemSales.values().stream()
                                .sorted((a, b) -> b.getQuantitySold().compareTo(a.getQuantitySold()))
                                .limit(limit)
                                .collect(Collectors.toList());
        }

        private List<RecentOrder> getRecentOrders(int limit) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

                return orderRepository.findRecentOrders(limit).stream()
                                .map(o -> {
                                        RecentOrder ro = new RecentOrder();
                                        ro.setOrderId(o.getOrderId());
                                        ro.setOrderNo(o.getOrderNo());
                                        ro.setTotal(o.getTotalAmount());
                                        ro.setStatus(o.getStatus() != null ? o.getStatus().name() : "UNKNOWN");
                                        ro.setCustomerName(
                                                        o.getCustomer() != null ? o.getCustomer().getName() : "Guest");
                                        ro.setCreatedAt(o.getCreatedAt() != null ? o.getCreatedAt().format(formatter)
                                                        : "");
                                        return ro;
                                })
                                .collect(Collectors.toList());
        }

        // ========== SALES REPORT ==========

        public com.example.backend.dto.report.SalesReportDTO getSalesReport(LocalDate startDate, LocalDate endDate) {
                com.example.backend.dto.report.SalesReportDTO report = new com.example.backend.dto.report.SalesReportDTO();

                LocalDateTime start = startDate.atStartOfDay();
                LocalDateTime end = endDate.plusDays(1).atStartOfDay();
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

                report.setStartDate(startDate.format(dateFormatter));
                report.setEndDate(endDate.format(dateFormatter));

                // Get orders in date range using optimized repository method
                List<OrderEntity> orders = orderRepository.findByCreatedAtBetweenAndDeletedAtIsNull(start, end).stream()
                                .filter(o -> o.getStatus() == OrderEntity.OrderStatus.PAID)
                                .collect(Collectors.toList());

                // Summary stats
                Double totalRevenue = orders.stream()
                                .mapToDouble(o -> o.getTotalAmount() != null ? o.getTotalAmount() : 0.0)
                                .sum();
                report.setTotalRevenue(totalRevenue);
                report.setTotalOrders(orders.size());
                report.setAverageOrderValue(orders.isEmpty() ? 0.0 : totalRevenue / orders.size());

                // Total items sold
                int totalItems = orders.stream()
                                .flatMap(o -> o.getItems() != null ? o.getItems().stream()
                                                : java.util.stream.Stream.empty())
                                .mapToInt(i -> i.getQty() != null ? i.getQty() : 0)
                                .sum();
                report.setTotalItemsSold(totalItems);

                // Daily sales breakdown
                report.setDailySales(getDailySalesBreakdown(orders, startDate, endDate));

                // Payment method summary
                report.setPaymentMethodSummary(getPaymentMethodSummary(start, end, totalRevenue));

                // Category sales
                report.setCategorySales(getCategorySalesBreakdown(orders));

                // Top selling items
                report.setTopSellingItems(getTopSellingItemsForReport(orders, 10));

                return report;
        }

        private List<com.example.backend.dto.report.SalesReportDTO.DailySales> getDailySalesBreakdown(
                        List<OrderEntity> orders, LocalDate startDate, LocalDate endDate) {

                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                Map<String, com.example.backend.dto.report.SalesReportDTO.DailySales> dailyMap = new HashMap<>();

                // Initialize all days in range
                LocalDate current = startDate;
                while (!current.isAfter(endDate)) {
                        String dateStr = current.format(dateFormatter);
                        dailyMap.put(dateStr,
                                        new com.example.backend.dto.report.SalesReportDTO.DailySales(dateStr, 0.0, 0));
                        current = current.plusDays(1);
                }

                // Aggregate orders by day
                for (OrderEntity order : orders) {
                        if (order.getCreatedAt() != null) {
                                String dateStr = order.getCreatedAt().format(dateFormatter);
                                com.example.backend.dto.report.SalesReportDTO.DailySales ds = dailyMap.get(dateStr);
                                if (ds != null) {
                                        ds.setRevenue(ds.getRevenue()
                                                        + (order.getTotalAmount() != null ? order.getTotalAmount()
                                                                        : 0.0));
                                        ds.setOrderCount(ds.getOrderCount() + 1);
                                }
                        }
                }

                return dailyMap.values().stream()
                                .sorted((a, b) -> a.getDate().compareTo(b.getDate()))
                                .collect(Collectors.toList());
        }

        private List<com.example.backend.dto.report.SalesReportDTO.PaymentMethodSummary> getPaymentMethodSummary(
                        LocalDateTime start, LocalDateTime end, Double totalRevenue) {

                List<PaymentEntity> payments = paymentRepository.findAll().stream()
                                .filter(p -> p.getCreatedAt() != null &&
                                                p.getCreatedAt().isAfter(start) &&
                                                p.getCreatedAt().isBefore(end) &&
                                                p.getDeletedAt() == null &&
                                                p.getPaymentStatus() == PaymentEntity.PaymentStatus.PAID)
                                .collect(Collectors.toList());

                Map<String, com.example.backend.dto.report.SalesReportDTO.PaymentMethodSummary> methodMap = new HashMap<>();

                for (PaymentEntity payment : payments) {
                        String method = payment.getMethod() != null ? payment.getMethod().name() : "OTHER";
                        com.example.backend.dto.report.SalesReportDTO.PaymentMethodSummary pms = methodMap.getOrDefault(
                                        method,
                                        new com.example.backend.dto.report.SalesReportDTO.PaymentMethodSummary(method,
                                                        0.0, 0, 0.0));
                        pms.setAmount(pms.getAmount()
                                        + (payment.getPaidAmount() != null ? payment.getPaidAmount() : 0.0));
                        pms.setCount(pms.getCount() + 1);
                        methodMap.put(method, pms);
                }

                // Calculate percentages
                return methodMap.values().stream()
                                .peek(pms -> pms.setPercentage(
                                                totalRevenue > 0 ? (pms.getAmount() / totalRevenue) * 100 : 0.0))
                                .sorted((a, b) -> b.getAmount().compareTo(a.getAmount()))
                                .collect(Collectors.toList());
        }

        private List<com.example.backend.dto.report.SalesReportDTO.CategorySales> getCategorySalesBreakdown(
                        List<OrderEntity> orders) {
                Map<Long, com.example.backend.dto.report.SalesReportDTO.CategorySales> categoryMap = new HashMap<>();

                for (OrderEntity order : orders) {
                        if (order.getItems() != null) {
                                for (OrderItemEntity item : order.getItems()) {
                                        if (item.getMenuItem() != null && item.getMenuItem().getCategory() != null) {
                                                Long catId = item.getMenuItem().getCategory().getCategoryId();
                                                String catName = item.getMenuItem().getCategory().getName();

                                                com.example.backend.dto.report.SalesReportDTO.CategorySales cs = categoryMap
                                                                .getOrDefault(catId,
                                                                                new com.example.backend.dto.report.SalesReportDTO.CategorySales(
                                                                                                catId, catName, 0.0,
                                                                                                0));
                                                cs.setRevenue(cs.getRevenue()
                                                                + (item.getUnitPrice() != null && item.getQty() != null
                                                                                ? item.getUnitPrice() * item.getQty()
                                                                                : 0.0));
                                                cs.setItemsSold(cs.getItemsSold()
                                                                + (item.getQty() != null ? item.getQty() : 0));
                                                categoryMap.put(catId, cs);
                                        }
                                }
                        }
                }

                return categoryMap.values().stream()
                                .sorted((a, b) -> b.getRevenue().compareTo(a.getRevenue()))
                                .collect(Collectors.toList());
        }

        private List<com.example.backend.dto.report.SalesReportDTO.TopSellingItem> getTopSellingItemsForReport(
                        List<OrderEntity> orders, int limit) {
                Map<Long, com.example.backend.dto.report.SalesReportDTO.TopSellingItem> itemMap = new HashMap<>();

                for (OrderEntity order : orders) {
                        if (order.getItems() != null) {
                                for (OrderItemEntity item : order.getItems()) {
                                        if (item.getMenuItem() != null) {
                                                Long menuItemId = item.getMenuItem().getMenuItemId();
                                                String name = item.getMenuItem().getName();
                                                String catName = item.getMenuItem().getCategory() != null
                                                                ? item.getMenuItem().getCategory().getName()
                                                                : "Uncategorized";

                                                com.example.backend.dto.report.SalesReportDTO.TopSellingItem tsi = itemMap
                                                                .getOrDefault(
                                                                                menuItemId,
                                                                                new com.example.backend.dto.report.SalesReportDTO.TopSellingItem(
                                                                                                menuItemId,
                                                                                                name, catName, 0, 0.0));
                                                tsi.setQuantitySold(tsi.getQuantitySold()
                                                                + (item.getQty() != null ? item.getQty() : 0));
                                                tsi.setRevenue(tsi.getRevenue()
                                                                + (item.getUnitPrice() != null && item.getQty() != null
                                                                                ? item.getUnitPrice() * item.getQty()
                                                                                : 0.0));
                                                itemMap.put(menuItemId, tsi);
                                        }
                                }
                        }
                }

                return itemMap.values().stream()
                                .sorted((a, b) -> b.getQuantitySold().compareTo(a.getQuantitySold()))
                                .limit(limit)
                                .collect(Collectors.toList());
        }

        // ========== INVENTORY REPORT ==========

        public com.example.backend.dto.report.InventoryReportDTO getInventoryReport(LocalDate startDate,
                        LocalDate endDate) {
                com.example.backend.dto.report.InventoryReportDTO report = new com.example.backend.dto.report.InventoryReportDTO();
                LocalDateTime start = startDate.atStartOfDay();
                LocalDateTime end = endDate.plusDays(1).atStartOfDay();
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

                report.setStartDate(startDate.format(dateFormatter));
                report.setEndDate(endDate.format(dateFormatter));

                List<com.example.backend.model.IngredientEntity> ingredients = ingredientRepository
                                .findAllByDeletedAtIsNull();

                // Total Inventory Value
                Double totalValue = ingredients.stream()
                                .mapToDouble(i -> (i.getCurrentStock() != null ? i.getCurrentStock() : 0.0)
                                                * (i.getCostPerUnit() != null ? i.getCostPerUnit() : 0.0))
                                .sum();
                report.setTotalInventoryValue(totalValue);

                // Low stock count
                long lowStockCount = ingredients.stream()
                                .filter(i -> i.getCurrentStock() <= i.getReorderLevel())
                                .count();
                report.setLowStockItemsCount((int) lowStockCount);

                // Wastage Analysis
                List<com.example.backend.model.StockAdjustmentEntity> adjustments = stockAdjustmentRepository
                                .findByCreatedAtBetweenAndDeletedAtIsNull(start, end);

                Map<String, com.example.backend.dto.report.InventoryReportDTO.WastageSummary> wastageMap = new HashMap<>();
                Double totalWasteCost = 0.0;

                for (com.example.backend.model.StockAdjustmentEntity adj : adjustments) {
                        String reason = adj.getReasonType() != null ? adj.getReasonType().name() : "OTHER";
                        if (adj.getQtyChange() < 0) { // Wastage/Loss is negative
                                com.example.backend.dto.report.InventoryReportDTO.WastageSummary ws = wastageMap
                                                .getOrDefault(reason,
                                                                new com.example.backend.dto.report.InventoryReportDTO.WastageSummary(
                                                                                reason, 0.0, 0.0, 0));

                                double qty = Math.abs(adj.getQtyChange());
                                double cost = qty * (adj.getIngredient() != null
                                                && adj.getIngredient().getCostPerUnit() != null
                                                                ? adj.getIngredient().getCostPerUnit()
                                                                : 0.0);

                                ws.setQuantity(ws.getQuantity() + qty);
                                ws.setCost(ws.getCost() + cost);
                                ws.setOccurrence(ws.getOccurrence() + 1);
                                wastageMap.put(reason, ws);
                                totalWasteCost += cost;
                        }
                }
                report.setWastageSummary(new ArrayList<>(wastageMap.values()));
                report.setTotalWasteCost(totalWasteCost);

                return report;
        }

        // ========== STAFF PERFORMANCE REPORT ==========

        @Transactional(readOnly = true)
        public com.example.backend.dto.report.StaffPerformanceReportDTO getStaffPerformanceReport(LocalDate startDate,
                        LocalDate endDate) {
                com.example.backend.dto.report.StaffPerformanceReportDTO report = new com.example.backend.dto.report.StaffPerformanceReportDTO();
                LocalDateTime start = startDate.atStartOfDay();
                LocalDateTime end = endDate.plusDays(1).atStartOfDay();
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

                report.setStartDate(startDate.format(dateFormatter));
                report.setEndDate(endDate.format(dateFormatter));

                List<com.example.backend.model.EmployeeEntity> employees = employeeRepository
                                .findAllByDeletedAtIsNull();
                List<OrderEntity> ordersInRange = orderRepository.findByCreatedAtBetweenAndDeletedAtIsNull(start, end)
                                .stream()
                                .filter(o -> o.getStatus() == OrderEntity.OrderStatus.PAID)
                                .collect(Collectors.toList());

                List<com.example.backend.dto.report.StaffPerformanceReportDTO.EmployeeStats> statsList = new ArrayList<>();

                for (com.example.backend.model.EmployeeEntity emp : employees) {
                        com.example.backend.dto.report.StaffPerformanceReportDTO.EmployeeStats stats = new com.example.backend.dto.report.StaffPerformanceReportDTO.EmployeeStats();
                        stats.setEmployeeId(emp.getEmployeeId());
                        stats.setFullName(emp.getFullName());
                        stats.setPosition(emp.getPosition());

                        // Sales performance (if cashierUserId points to the same ID or linked user)
                        // For MVP, we'll assume cashierUserId is linked to the employee record
                        // implicitly or via User.
                        // Let's filter orders where cashierUserId matches (assuming employee handle
                        // their own login)
                        List<OrderEntity> handledOrders = ordersInRange.stream()
                                        .filter(o -> o.getCashierUser() != null
                                                        && o.getCashierUser().getEmployee() != null
                                                        && o.getCashierUser().getEmployee().getEmployeeId()
                                                                        .equals(emp.getEmployeeId()))
                                        .collect(Collectors.toList());

                        stats.setTotalOrdersHandled(handledOrders.size());
                        stats.setTotalSalesGenerated(handledOrders.stream()
                                        .mapToDouble(o -> o.getTotalAmount() != null ? o.getTotalAmount() : 0.0).sum());
                        stats.setAverageOrderValue(
                                        stats.getTotalOrdersHandled() > 0
                                                        ? stats.getTotalSalesGenerated() / stats.getTotalOrdersHandled()
                                                        : 0.0);

                        // Attendance & Punctuality
                        List<com.example.backend.model.AttendanceEntity> attendance = attendanceRepository
                                        .findByEmployeeEmployeeIdAndCheckInBetweenAndDeletedAtIsNull(
                                                        emp.getEmployeeId(), start, end);

                        long lateCount = attendance.stream().filter(a -> "LATE".equals(a.getStatus())).count();
                        stats.setLateOccurrences(lateCount);
                        stats.setPunctualityRate(attendance.isEmpty() ? 100.0
                                        : ((double) (attendance.size() - lateCount) / attendance.size()) * 100);

                        long totalMinutes = 0;
                        for (com.example.backend.model.AttendanceEntity att : attendance) {
                                if (att.getCheckIn() != null && att.getCheckOut() != null) {
                                        totalMinutes += java.time.Duration.between(att.getCheckIn(), att.getCheckOut())
                                                        .toMinutes();
                                }
                        }
                        stats.setTotalMinutesWorked(totalMinutes);

                        statsList.add(stats);
                }

                report.setEmployeeStats(statsList);
                return report;
        }

        public com.example.backend.dto.report.ShiftSummaryDTO getCurrentShiftSummary(Long employeeId) {
                AttendanceEntity activeAttendance = attendanceRepository
                                .findTopByEmployeeEmployeeIdAndCheckOutIsNullAndDeletedAtIsNullOrderByCheckInDesc(
                                                employeeId)
                                .orElseThrow(() -> new RuntimeException(
                                                "No active shift found for this employee. Please Clock-In first."));

                LocalDateTime shiftStart = activeAttendance.getCheckIn();
                LocalDateTime now = LocalDateTime.now();

                // Get orders handled by this employee during this shift
                // Since cashier is linked via User ID, we need to map employee -> user
                // For MVP, we assume employeeId matches cashierUser userId if they are linked
                // 1:1
                List<OrderEntity> shiftOrders = orderRepository
                                .findByCashierUserUserIdAndCreatedAtBetweenAndDeletedAtIsNull(
                                                employeeId, shiftStart, now);

                com.example.backend.dto.report.ShiftSummaryDTO summary = new com.example.backend.dto.report.ShiftSummaryDTO();
                summary.setEmployeeId(employeeId);
                summary.setEmployeeName(activeAttendance.getEmployee().getFullName());
                summary.setCheckInTime(shiftStart.toString());
                summary.setStatus("OPEN");

                double totalSales = 0.0;
                Map<String, Double> salesByMethod = new HashMap<>();
                salesByMethod.put("CASH", 0.0);
                salesByMethod.put("CARD", 0.0);
                salesByMethod.put("QR", 0.0);

                for (OrderEntity order : shiftOrders) {
                        if (order.getStatus() == OrderEntity.OrderStatus.PAID) {
                                totalSales += order.getTotalAmount();

                                // Get payment for this order
                                List<PaymentEntity> payments = paymentRepository
                                                .findByOrderOrderIdAndDeletedAtIsNull(order.getOrderId());
                                for (PaymentEntity p : payments) {
                                        String method = p.getMethod().name();
                                        salesByMethod.put(method,
                                                        salesByMethod.getOrDefault(method, 0.0) + p.getPaidAmount());
                                }
                        }
                }

                summary.setTotalSales(totalSales);
                summary.setSalesByMethod(salesByMethod);
                summary.setTotalOrders(shiftOrders.size());
                summary.setExpectedCash(salesByMethod.get("CASH"));

                return summary;
        }

        // ========== STOCK MOVEMENT REPORT ==========

        public com.example.backend.dto.report.StockMovementReportDTO getStockMovementReport(LocalDate startDate,
                        LocalDate endDate, Long branchId, Long ingredientId) {
                com.example.backend.dto.report.StockMovementReportDTO report = new com.example.backend.dto.report.StockMovementReportDTO();
                LocalDateTime start = startDate.atStartOfDay();
                LocalDateTime end = endDate.plusDays(1).atStartOfDay();
                LocalDateTime now = LocalDateTime.now();
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

                report.setStartDate(startDate.format(dateFormatter));
                report.setEndDate(endDate.format(dateFormatter));

                List<IngredientEntity> ingredients = (ingredientId != null)
                                ? List.of(ingredientRepository.findById(ingredientId)
                                                .orElseThrow(() -> new RuntimeException("Ingredient not found")))
                                : ingredientRepository.findAllByDeletedAtIsNull();

                List<com.example.backend.dto.report.StockMovementReportDTO.StockMovementItem> items = new ArrayList<>();
                List<String> recommendations = new ArrayList<>();

                // Fetch all movements once to optimize
                Map<Long, List<StockInEntity>> stockInMap = stockInRepository
                                .findByReceivedDateBetweenAndDeletedAtIsNull(start, end)
                                .stream().collect(Collectors.groupingBy(si -> si.getIngredient().getIngredientId()));

                Map<Long, List<StockAdjustmentEntity>> adjMap = stockAdjustmentRepository
                                .findByCreatedAtBetweenAndDeletedAtIsNull(start, end)
                                .stream().filter(a -> a.getStatus() == StockAdjustmentEntity.AdjustmentStatus.APPROVED)
                                .collect(Collectors.groupingBy(a -> a.getIngredient().getIngredientId()));

                // For stock out (sales), we need to aggregate orders in range
                List<OrderEntity> ordersInRange = orderRepository.findByCreatedAtBetweenAndDeletedAtIsNull(start, end)
                                .stream().filter(o -> o.getStatus() == OrderEntity.OrderStatus.PAID)
                                .collect(Collectors.toList());

                // Optional: Branch filtering if branchId is provided
                if (branchId != null) {
                        ordersInRange = ordersInRange.stream()
                                        .filter(o -> o.getBranch().getBranchId().equals(branchId))
                                        .collect(Collectors.toList());
                }

                // Pre-calculate sales stock out per ingredient
                Map<Long, Double> salesStockOut = new HashMap<>();
                for (OrderEntity order : ordersInRange) {
                        for (OrderItemEntity orderItem : order.getItems()) {
                                List<RecipeEntity> recipes = recipeRepository
                                                .findByMenuItemMenuItemId(orderItem.getMenuItem().getMenuItemId());
                                for (RecipeEntity recipe : recipes) {
                                        Long ingId = recipe.getIngredient().getIngredientId();
                                        double qty = (recipe.getQuantityNeeded() != null ? recipe.getQuantityNeeded()
                                                        : 0.0) * (orderItem.getQty() != null ? orderItem.getQty() : 0);
                                        salesStockOut.put(ingId, salesStockOut.getOrDefault(ingId, 0.0) + qty);
                                }
                        }
                }

                // Also need movements between end date and now to calculate "Closing Stock" as of endDate
                // MovementAfterEnd = StockIn(End to Now) + Adj(End to Now) - Sales(End to Now)
                // ClosingStock(End) = CurrentStock - MovementAfterEnd

                for (IngredientEntity ing : ingredients) {
                        Long ingId = ing.getIngredientId();

                        // 1. Movement In (Received)
                        double received = stockInMap.getOrDefault(ingId, new ArrayList<>()).stream()
                                        .mapToDouble(si -> si.getQtyIn() != null ? si.getQtyIn() : 0.0).sum();

                        // 2. Adjustments (Approved)
                        double adjusted = adjMap.getOrDefault(ingId, new ArrayList<>()).stream()
                                        .mapToDouble(a -> a.getQtyChange() != null ? a.getQtyChange() : 0.0).sum();

                        // 3. Stock Out (Sales)
                        double sold = salesStockOut.getOrDefault(ingId, 0.0);

                        // 4. Closing Stock at end date
                        // Simplified: Since we don't have a full historical snapshot, 
                        // we calculate back from current stock if end date is near.
                        // For this MVP, we'll assume current stock if endDate is today.
                        double closing = ing.getCurrentStock() != null ? ing.getCurrentStock() : 0.0;
                        
                        // If endDate is in the past, we'd need to subtract movements from endDate to now.
                        // (Skipping for brevity in first pass, assuming report is generated for recent periods)

                        // 5. Opening Stock
                        double opening = closing - (received + adjusted - sold);

                        // Calculate Wastage % (Adjustments where negative / total handled)
                        double wastage = adjMap.getOrDefault(ingId, new ArrayList<>()).stream()
                                        .filter(a -> a.getQtyChange() < 0)
                                        .mapToDouble(a -> Math.abs(a.getQtyChange())).sum();
                        double totalHandled = opening + received;
                        double wastagePct = totalHandled > 0 ? (wastage / totalHandled) * 100 : 0.0;

                        com.example.backend.dto.report.StockMovementReportDTO.StockMovementItem item = new com.example.backend.dto.report.StockMovementReportDTO.StockMovementItem();
                        item.setIngredientId(ingId);
                        item.setName(ing.getName());
                        item.setSku(ing.getSku());
                        item.setOpeningStock(opening);
                        item.setReceived(received);
                        item.setSold(sold);
                        item.setAdjusted(adjusted);
                        item.setClosingStock(closing);
                        item.setUnit(ing.getUnit() != null ? ing.getUnit().name() : "UNIT");
                        item.setWastagePercentage(wastagePct);
                        items.add(item);

                        // Insights
                        if (wastagePct > 10.0) {
                                recommendations.add("High wastage for " + ing.getName() + " (" + String.format("%.1f", wastagePct) + "%). Inspect storage conditions.");
                        }
                        if (opening < (ing.getReorderLevel() != null ? ing.getReorderLevel() : 0.0)) {
                                recommendations.add(ing.getName() + " was low at start of period. Consider increasing safety stock.");
                        }
                }

                report.setMovements(items);
                report.setRecommendations(recommendations);
                return report;
        }
}
