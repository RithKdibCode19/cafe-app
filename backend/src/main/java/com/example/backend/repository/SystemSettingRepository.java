package com.example.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.backend.model.SystemSettingEntity;

@Repository
public interface SystemSettingRepository extends JpaRepository<SystemSettingEntity, Long> {
    Optional<SystemSettingEntity> findByKey(String key);
}
