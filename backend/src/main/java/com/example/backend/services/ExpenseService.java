package com.example.backend.services;

import com.example.backend.dto.ExpenseRequestDTO;
import com.example.backend.dto.ExpenseResponseDTO;
import com.example.backend.model.BranchEntity;
import com.example.backend.model.ExpenseEntity;
import com.example.backend.model.UserEntity;
import com.example.backend.repository.BranchRepository;
import com.example.backend.repository.ExpenseRepository;
import com.example.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final BranchRepository branchRepository;
    private final UserRepository userRepository;

    public ExpenseService(ExpenseRepository expenseRepository, BranchRepository branchRepository, UserRepository userRepository) {
        this.expenseRepository = expenseRepository;
        this.branchRepository = branchRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ExpenseResponseDTO createExpense(ExpenseRequestDTO request) {
        BranchEntity branch = branchRepository.findById(request.getBranchId())
                .orElseThrow(() -> new RuntimeException("Branch not found"));

        UserEntity user = userRepository.findById(request.getRecordedById())
                .orElseThrow(() -> new RuntimeException("User not found"));

        ExpenseEntity expense = new ExpenseEntity();
        expense.setTitle(request.getTitle());
        expense.setDescription(request.getDescription());
        expense.setAmount(request.getAmount());
        expense.setDate(request.getDate() != null ? request.getDate() : LocalDate.now());
        expense.setCategory(request.getCategory());
        expense.setBranch(branch);
        expense.setRecordedBy(user);

        ExpenseEntity saved = expenseRepository.save(expense);
        return mapToResponse(saved);
    }

    public List<ExpenseResponseDTO> getAllExpenses() {
        return expenseRepository.findAll().stream()
                .filter(e -> e.getDeletedAt() == null)
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<ExpenseResponseDTO> getExpensesByBranch(Long branchId) {
        return expenseRepository.findByBranchBranchIdAndDeletedAtIsNull(branchId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private ExpenseResponseDTO mapToResponse(ExpenseEntity entity) {
        ExpenseResponseDTO dto = new ExpenseResponseDTO();
        dto.setExpenseId(entity.getExpenseId());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setAmount(entity.getAmount());
        dto.setDate(entity.getDate());
        dto.setCategory(entity.getCategory());
        dto.setBranchId(entity.getBranch().getBranchId());
        dto.setBranchName(entity.getBranch().getName());
        dto.setRecordedByName(entity.getRecordedBy() != null ? entity.getRecordedBy().getUserName() : "System");
        return dto;
    }

    @Transactional
    public void deleteExpense(Long id) {
        ExpenseEntity expense = expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expense not found"));
        expense.setDeletedAt(java.time.LocalDateTime.now());
        expenseRepository.save(expense);
    }
}
