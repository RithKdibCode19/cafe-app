package com.example.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.backend.model.MenuItemEntity;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItemEntity, Long> {
    @org.springframework.cache.annotation.Cacheable("menu-items")
    @Query("SELECT m FROM MenuItemEntity m WHERE m.deletedAt IS NULL")
    List<MenuItemEntity> findAllActive();

    java.util.Optional<MenuItemEntity> findByNameAndDeletedAtIsNull(String name);

    @Query("SELECT m FROM MenuItemEntity m WHERE m.deletedAt IS NULL AND LOWER(m.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<MenuItemEntity> searchByName(@org.springframework.data.repository.query.Param("query") String query);
}
