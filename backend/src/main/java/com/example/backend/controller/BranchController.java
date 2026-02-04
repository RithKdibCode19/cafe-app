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

import com.example.backend.dto.BranchRequestDTO;
import com.example.backend.dto.BranchResponseDTO;
import com.example.backend.services.BranchService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/branches")
@RequiredArgsConstructor
public class BranchController {

    private final BranchService branchService;

    @GetMapping
    public ResponseEntity<List<BranchResponseDTO>> getAllBranches() {
        List<BranchResponseDTO> response = branchService.getAllBranches();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BranchResponseDTO> getBranchById(@PathVariable Long id) {
        BranchResponseDTO response = branchService.getBranchById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/add")
    public ResponseEntity<BranchResponseDTO> createBranch(@Valid @RequestBody BranchRequestDTO request) {
        BranchResponseDTO response = branchService.create(request);
        return ResponseEntity.ok(response);
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<BranchResponseDTO> updateBranch(@PathVariable Long id, @Valid @RequestBody BranchRequestDTO request) {
        BranchResponseDTO response = branchService.update(id, request);
        return ResponseEntity.ok(response);
    }
    @PutMapping("/delete/{id}")
    public ResponseEntity<BranchResponseDTO> deleteBranch(@PathVariable Long id) {
        BranchResponseDTO response = branchService.delete(id);
        return ResponseEntity.ok(response);
    }
}
