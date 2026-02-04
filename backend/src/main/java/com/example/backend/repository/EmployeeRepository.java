package com.example.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.backend.model.EmployeeEntity;

@Repository
public interface EmployeeRepository extends JpaRepository<EmployeeEntity, Long> {
    @Query("SELECT e FROM EmployeeEntity e WHERE e.status = 'ACTIVE' ")
    List<EmployeeEntity> findAllActiveEmployees();

    @Query("SELECT e FROM EmployeeEntity e WHERE e.employeeId = :employeeId AND e.status = 'ACTIVE'")
    EmployeeEntity findActiveEmployeeById(Long employeeId);

    List<EmployeeEntity> findAllByDeletedAtIsNull();
}
