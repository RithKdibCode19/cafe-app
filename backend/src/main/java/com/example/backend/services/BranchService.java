package com.example.backend.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.backend.dto.BranchRequestDTO;
import com.example.backend.dto.BranchResponseDTO;
import com.example.backend.mapper.BranchMapper;
import com.example.backend.model.BranchEntity;
import com.example.backend.repository.BranchRepository;

import jakarta.transaction.Transactional;

@Service
public class BranchService {
    private final BranchRepository branchRepository;
    private final BranchMapper branchMapper;
    public BranchService(BranchRepository branchRepository, BranchMapper branchMapper) {
        this.branchRepository = branchRepository;
        this.branchMapper = branchMapper;
    }
    public List<BranchResponseDTO> getAllBranches() {
        List<BranchEntity> branches = branchRepository.findAllActiveBranches();
        return branches.stream()
                .map(branchMapper::toResponseDTO)
                .toList();
    }
    public BranchResponseDTO getBranchById(Long id) {
        BranchEntity branch = branchRepository.findActiveBranchById(id);
        return branchMapper.toResponseDTO(branch);
    }
    @Transactional
    public BranchResponseDTO create(BranchRequestDTO branchRequestDTO) {
        BranchEntity branchEntity = branchMapper.toEntity(branchRequestDTO);
        BranchEntity savedBranch = branchRepository.save(branchEntity);
        return branchMapper.toResponseDTO(savedBranch);
    }
    @Transactional
    public BranchResponseDTO update(Long id, BranchRequestDTO branchRequestDTO) {
        BranchEntity existingBranch = branchRepository.findActiveBranchById(id);
        branchMapper.updateEntityFromDTO(branchRequestDTO, existingBranch);
        BranchEntity updatedBranch = branchRepository.save(existingBranch);
        return branchMapper.toResponseDTO(updatedBranch);
    }
    @Transactional
    public BranchResponseDTO delete(Long id) {
        BranchEntity existingBranch = branchRepository.findActiveBranchById(id);
        existingBranch.setDeletedAt(LocalDateTime.now());
        BranchEntity deletedBranch = branchRepository.save(existingBranch);
        return branchMapper.toResponseDTO(deletedBranch);
    }
}
