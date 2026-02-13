package com.example.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.backend.model.CategoryEntity;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
    @Query("""
    SELECT c FROM CategoryEntity c WHERE c.deletedAt IS NULL
""")
    List<CategoryEntity> findAllActive();

    java.util.Optional<CategoryEntity> findByName(String name);

    @Query("""
    SELECT DISTINCT c FROM CategoryEntity c
    LEFT JOIN FETCH c.children
    WHERE c.parent IS NULL AND c.deletedAt IS NULL
""")
    List<CategoryEntity> findRoots();

    java.util.Optional<CategoryEntity> findByNameAndDeletedAtIsNull(String name);
}
