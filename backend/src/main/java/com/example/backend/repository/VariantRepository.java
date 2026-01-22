package com.example.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.backend.model.VariantEntity;

@Repository
public interface VariantRepository extends JpaRepository<VariantEntity, Long> {

}
