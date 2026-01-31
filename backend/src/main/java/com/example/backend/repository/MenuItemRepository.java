package com.example.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.backend.model.MenuItemEntity;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItemEntity, Long> {
    @Query("SELECT m FROM MenuItemEntity m WHERE m.deletedAt IS NULL")
    List<MenuItemEntity> findAllActive();
}
