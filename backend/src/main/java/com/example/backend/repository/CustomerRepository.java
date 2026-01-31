package com.example.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.backend.model.CustomerEntity;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {
    @Query("SELECT c FROM CustomerEntity c WHERE c.id = ?1 AND c.deletedAt IS NULL")
    CustomerEntity findActive();
    @Query("SELECT c FROM CustomerEntity c WHERE c.id = ?1")
    CustomerEntity findByIdIncludingDeleted(Long id);
}
