package com.example.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.backend.model.DrawerActionEntity;

@Repository
public interface DrawerActionRepository extends JpaRepository<DrawerActionEntity, Long> {
    List<DrawerActionEntity> findByUserUserId(Long userId);
    List<DrawerActionEntity> findByActionTimeBetween(java.time.LocalDateTime start, java.time.LocalDateTime end);
}
