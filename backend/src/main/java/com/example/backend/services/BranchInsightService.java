package com.example.backend.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.example.backend.dto.BranchPerformanceDTO;
import com.example.backend.model.BranchEntity;
import com.example.backend.repository.BranchRepository;
import com.example.backend.repository.OrderRepository;

@Service
public class BranchInsightService {

    private final BranchRepository branchRepository;
    private final OrderRepository orderRepository;

    public BranchInsightService(BranchRepository branchRepository, OrderRepository orderRepository) {
        this.branchRepository = branchRepository;
        this.orderRepository = orderRepository;
    }

    public List<BranchPerformanceDTO> getBranchComparison(String range) {
        LocalDateTime start;
        LocalDateTime end = LocalDateTime.now();

        switch (range.toUpperCase()) {
            case "WEEK":
                start = end.minusWeeks(1);
                break;
            case "MONTH":
                start = end.minusMonths(1);
                break;
            case "TODAY":
            default:
                start = end.toLocalDate().atStartOfDay();
                break;
        }

        List<BranchEntity> branches = branchRepository.findAllActiveBranches();
        List<BranchPerformanceDTO> comparison = new ArrayList<>();

        for (BranchEntity branch : branches) {
            Long branchId = branch.getBranchId();
            Double totalSales = orderRepository.calculateTotalSalesForBranchAndPeriod(branchId, start, end);
            long orderCount = orderRepository.countOrdersForBranchAndPeriod(branchId, start, end);
            
            String topItem = "N/A";
            List<Object[]> topItems = orderRepository.findTopMenuItemForBranch(branchId, PageRequest.of(0, 1));
            if (!topItems.isEmpty()) {
                topItem = (String) topItems.get(0)[0];
            }

            comparison.add(BranchPerformanceDTO.builder()
                    .branchId(branchId)
                    .branchName(branch.getName())
                    .totalSales(totalSales != null ? totalSales : 0.0)
                    .orderCount(orderCount)
                    .avgTransactionValue(orderCount > 0 ? (totalSales != null ? totalSales : 0.0) / orderCount : 0.0)
                    .topMenuItem(topItem)
                    .build());
        }

        return comparison;
    }
}
