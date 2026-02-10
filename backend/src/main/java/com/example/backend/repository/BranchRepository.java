package com.example.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.backend.model.BranchEntity;

@Repository
public interface BranchRepository extends JpaRepository<BranchEntity, Long> {
    @Query("SELECT b FROM BranchEntity b WHERE b.deletedAt IS NULL")
    List<BranchEntity> findAllActiveBranches();

    @Query("SELECT b FROM BranchEntity b WHERE b.branchId = ?1 AND b.deletedAt IS NULL")
    BranchEntity findActiveBranchById(Long id);

    Optional<BranchEntity> findByCode(String code);
}
